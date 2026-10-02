package com.example.core.network

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.atomic.AtomicInteger

data class RealtimeChangeEvent(
    val table: String,
    val eventType: String, // INSERT, UPDATE, DELETE
    val record: JSONObject?,
    val oldRecord: JSONObject?
)

class SupabaseRealtimeManager(
    private val config: SupabaseConfig,
    private val client: OkHttpClient
) {
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var webSocket: WebSocket? = null
    private var heartbeatJob: Job? = null
    private val refCounter = AtomicInteger(1)

    private val _eventsFlow = MutableSharedFlow<RealtimeChangeEvent>(extraBufferCapacity = 64)
    val eventsFlow: SharedFlow<RealtimeChangeEvent> = _eventsFlow.asSharedFlow()

    private var isConnected = false

    fun connect() {
        if (isConnected || !config.isConfigured()) return

        val url = config.getUrl()
        val wsUrl = if (url.startsWith("https://")) {
            url.replace("https://", "wss://") + "/realtime/v1/websocket?apikey=${config.getAnonKey()}&vsn=1.0.0"
        } else {
            url.replace("http://", "ws://") + "/realtime/v1/websocket?apikey=${config.getAnonKey()}&vsn=1.0.0"
        }

        val request = Request.Builder().url(wsUrl).build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                isConnected = true
                Log.d("SupabaseRealtime", "Connected to Realtime WebSocket")
                listOf("messages", "conversations", "conversation_members", "message_receipts", "profiles")
                    .forEach(::joinTableChannel)
                startHeartbeat()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                handleIncomingMessage(text)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                isConnected = false
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                isConnected = false
                Log.w("SupabaseRealtime", "WebSocket failure: ${t.message}. Reconnecting in 5s...")
                heartbeatJob?.cancel()
                scope.launch {
                    delay(5000)
                    if (scope.isActive) connect()
                }
            }
        })
    }

    private fun joinTableChannel(table: String) {
        val ref = refCounter.getAndIncrement().toString()
        val joinPayload = JSONObject().apply {
            put("topic", "realtime:public:$table")
            put("event", "phx_join")
            put("ref", ref)
            val payload = JSONObject().apply {
                val configObj = JSONObject().apply {
                    val changes = JSONArray().apply {
                        put(JSONObject().apply {
                            put("event", "*")
                            put("schema", "public")
                            put("table", table)
                        })
                    }
                    put("postgres_changes", changes)
                }
                put("config", configObj)
            }
            put("payload", payload)
        }

        webSocket?.send(joinPayload.toString())
    }

    private fun startHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = scope.launch {
            while (isActive && isConnected) {
                delay(25000)
                val hbPayload = JSONObject().apply {
                    put("topic", "phoenix")
                    put("event", "heartbeat")
                    put("payload", JSONObject())
                    put("ref", refCounter.getAndIncrement().toString())
                }
                webSocket?.send(hbPayload.toString())
            }
        }
    }

    private fun handleIncomingMessage(text: String) {
        try {
            val json = JSONObject(text)
            val event = json.optString("event")
            if (event == "postgres_changes") {
                val payload = json.optJSONObject("payload") ?: return
                val data = payload.optJSONObject("data") ?: return
                val table = data.optString("table")
                val eventType = data.optString("type")
                val record = data.optJSONObject("record")
                val oldRecord = data.optJSONObject("old_record")

                _eventsFlow.tryEmit(
                    RealtimeChangeEvent(
                        table = table,
                        eventType = eventType,
                        record = record,
                        oldRecord = oldRecord
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("SupabaseRealtime", "Error parsing realtime message: ${e.message}")
        }
    }

    fun disconnect() {
        heartbeatJob?.cancel()
        webSocket?.close(1000, "Normal Closure")
        isConnected = false
    }
}
