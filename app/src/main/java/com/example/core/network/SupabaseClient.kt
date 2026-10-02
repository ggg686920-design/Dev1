package com.example.core.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class SupabaseClient(private val config: SupabaseConfig) {

    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val original = chain.request()
            val token = config.getAccessToken()
            val anonKey = config.getAnonKey()

            val builder = original.newBuilder()
                .header("apikey", anonKey)

            if (!token.isNullOrBlank()) {
                builder.header("Authorization", "Bearer $token")
            } else {
                builder.header("Authorization", "Bearer $anonKey")
            }

            chain.proceed(builder.build())
        }
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    // ----------------------------------------------------
    // AUTHENTICATION APIS
    // ----------------------------------------------------

    suspend fun signUp(email: String, password: String, data: Map<String, Any>): JSONObject = withContext(Dispatchers.IO) {
        val url = "${config.getUrl()}/auth/v1/signup"
        val payload = JSONObject().apply {
            put("email", email)
            put("password", password)
            put("data", JSONObject(data))
        }

        val request = Request.Builder()
            .url(url)
            .post(payload.toString().toRequestBody(jsonMediaType))
            .build()

        executeAndParseObject(request)
    }

    suspend fun signInWithPassword(email: String, password: String): JSONObject = withContext(Dispatchers.IO) {
        val url = "${config.getUrl()}/auth/v1/token?grant_type=password"
        val payload = JSONObject().apply {
            put("email", email)
            put("password", password)
        }

        val request = Request.Builder()
            .url(url)
            .post(payload.toString().toRequestBody(jsonMediaType))
            .build()

        executeAndParseObject(request)
    }

    suspend fun signOut(): Boolean = withContext(Dispatchers.IO) {
        val url = "${config.getUrl()}/auth/v1/logout"
        val request = Request.Builder()
            .url(url)
            .post("{}".toRequestBody(jsonMediaType))
            .build()

        try {
            val response = okHttpClient.newCall(request).execute()
            response.isSuccessful
        } catch (_: Exception) {
            false
        } finally {
            config.clearSession()
        }
    }

    // ----------------------------------------------------
    // POSTGREST / DATABASE APIS
    // ----------------------------------------------------

    suspend fun postgrestGet(endpoint: String): JSONArray = withContext(Dispatchers.IO) {
        val fullUrl = "${config.getUrl()}/rest/v1/$endpoint"
        val request = Request.Builder()
            .url(fullUrl)
            .get()
            .build()

        executeAndParseArray(request)
    }

    suspend fun postgrestPost(endpoint: String, payload: JSONObject, returnRepresentation: Boolean = true): JSONArray = withContext(Dispatchers.IO) {
        val fullUrl = "${config.getUrl()}/rest/v1/$endpoint"
        val builder = Request.Builder()
            .url(fullUrl)
            .post(payload.toString().toRequestBody(jsonMediaType))

        if (returnRepresentation) {
            builder.header("Prefer", "return=representation")
        }

        val request = builder.build()
        executeAndParseArray(request)
    }

    suspend fun postgrestPatch(endpoint: String, payload: JSONObject): JSONArray = withContext(Dispatchers.IO) {
        val fullUrl = "${config.getUrl()}/rest/v1/$endpoint"
        val request = Request.Builder()
            .url(fullUrl)
            .patch(payload.toString().toRequestBody(jsonMediaType))
            .header("Prefer", "return=representation")
            .build()

        executeAndParseArray(request)
    }

    suspend fun postgrestDelete(endpoint: String): Boolean = withContext(Dispatchers.IO) {
        val fullUrl = "${config.getUrl()}/rest/v1/$endpoint"
        val request = Request.Builder()
            .url(fullUrl)
            .delete()
            .build()

        val response = okHttpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            val errBody = response.body?.string().orEmpty()
            throw IOException("Database Delete Error [${response.code}]: $errBody")
        }
        true
    }

    // ----------------------------------------------------
    // STORAGE APIS
    // ----------------------------------------------------

    suspend fun uploadFile(bucket: String, path: String, fileBytes: ByteArray, mimeType: String): String = withContext(Dispatchers.IO) {
        val cleanPath = path.removePrefix("/")
        val uploadUrl = "${config.getUrl()}/storage/v1/object/$bucket/$cleanPath"

        val body: RequestBody = fileBytes.toRequestBody(mimeType.toMediaTypeOrNull())
        val request = Request.Builder()
            .url(uploadUrl)
            .post(body)
            .header("x-upsert", "true")
            .build()

        val response = okHttpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            val errBody = response.body?.string().orEmpty()
            throw IOException("Storage Upload Error [${response.code}]: $errBody")
        }

        // Return public URL
        "${config.getUrl()}/storage/v1/object/public/$bucket/$cleanPath"
    }

    suspend fun createSignedUrl(bucket: String, path: String, expiresInSeconds: Int = 3600): String = withContext(Dispatchers.IO) {
        val cleanPath = path.removePrefix("/")
        val request = Request.Builder()
            .url("${config.getUrl()}/storage/v1/object/sign/$bucket/$cleanPath")
            .post(JSONObject().put("expiresIn", expiresInSeconds).toString().toRequestBody(jsonMediaType))
            .build()
        val response = okHttpClient.newCall(request).execute()
        val body = response.body?.string().orEmpty()
        if (!response.isSuccessful) {
            throw IOException("Storage Sign Error [${response.code}]: ${parseErrorMessage(body, response.code)}")
        }
        val signedPath = JSONObject(body).optString("signedURL").ifBlank {
            throw IOException("Storage returned no signed URL")
        }
        if (signedPath.startsWith("http")) signedPath else "${config.getUrl()}/storage/v1${signedPath}"
    }

    // ----------------------------------------------------
    // HELPERS
    // ----------------------------------------------------

    private fun executeAndParseObject(request: Request): JSONObject {
        val response = okHttpClient.newCall(request).execute()
        val responseBody = response.body?.string().orEmpty()

        if (!response.isSuccessful) {
            val message = parseErrorMessage(responseBody, response.code)
            throw IOException(message)
        }

        return if (responseBody.isBlank()) JSONObject() else JSONObject(responseBody)
    }

    private fun executeAndParseArray(request: Request): JSONArray {
        val response = okHttpClient.newCall(request).execute()
        val responseBody = response.body?.string().orEmpty()

        if (!response.isSuccessful) {
            val message = parseErrorMessage(responseBody, response.code)
            throw IOException(message)
        }

        return if (responseBody.isBlank()) {
            JSONArray()
        } else if (responseBody.trim().startsWith("[")) {
            JSONArray(responseBody)
        } else if (responseBody.trim().startsWith("{")) {
            JSONArray().put(JSONObject(responseBody))
        } else {
            JSONArray()
        }
    }

    private fun parseErrorMessage(body: String, code: Int): String {
        return try {
            val json = JSONObject(body)
            when {
                json.has("msg") -> json.getString("msg")
                json.has("message") -> json.getString("message")
                json.has("error_description") -> json.getString("error_description")
                json.has("error") -> json.getString("error")
                else -> "HTTP $code: $body"
            }
        } catch (_: Exception) {
            "HTTP $code: $body"
        }
    }
}
