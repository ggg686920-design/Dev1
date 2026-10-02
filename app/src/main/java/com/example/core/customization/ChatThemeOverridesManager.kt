package com.example.core.customization

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject

class ChatThemeOverridesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("dev_chat_overrides_prefs", Context.MODE_PRIVATE)

    private val _overrides = MutableStateFlow(loadOverrides())
    val overrides: StateFlow<Map<String, ConversationThemeOverride>> = _overrides.asStateFlow()

    private fun loadOverrides(): Map<String, ConversationThemeOverride> {
        val raw = prefs.getString("chat_overrides_json", null) ?: return emptyMap()
        val map = mutableMapOf<String, ConversationThemeOverride>()
        try {
            val root = JSONObject(raw)
            root.keys().forEach { cId ->
                val obj = root.getJSONObject(cId)
                map[cId] = ConversationThemeOverride(
                    conversationId = cId,
                    customWallpaperId = if (obj.has("wallpaperId")) obj.getString("wallpaperId") else null,
                    customThemeId = if (obj.has("themeId")) obj.getString("themeId") else null,
                    customBubbleRadiusDp = if (obj.has("bubbleRadius")) obj.getInt("bubbleRadius") else null,
                    customAccentColorHex = if (obj.has("accentHex")) obj.getString("accentHex") else null
                )
            }
        } catch (_: Exception) { }
        return map
    }

    fun setChatWallpaper(conversationId: String, wallpaperId: String) {
        val current = _overrides.value.toMutableMap()
        val existing = current[conversationId] ?: ConversationThemeOverride(conversationId)
        current[conversationId] = existing.copy(customWallpaperId = wallpaperId)
        saveOverrides(current)
    }

    fun setChatTheme(conversationId: String, themeId: String) {
        val current = _overrides.value.toMutableMap()
        val existing = current[conversationId] ?: ConversationThemeOverride(conversationId)
        current[conversationId] = existing.copy(customThemeId = themeId)
        saveOverrides(current)
    }

    fun resetChatTheme(conversationId: String) {
        val current = _overrides.value.toMutableMap()
        current.remove(conversationId)
        saveOverrides(current)
    }

    fun getOverrideForChat(conversationId: String): ConversationThemeOverride? {
        return _overrides.value[conversationId]
    }

    private fun saveOverrides(map: Map<String, ConversationThemeOverride>) {
        _overrides.value = map
        val root = JSONObject()
        map.forEach { (cId, ovr) ->
            val obj = JSONObject()
            ovr.customWallpaperId?.let { obj.put("wallpaperId", it) }
            ovr.customThemeId?.let { obj.put("themeId", it) }
            ovr.customBubbleRadiusDp?.let { obj.put("bubbleRadius", it) }
            ovr.customAccentColorHex?.let { obj.put("accentHex", it) }
            root.put(cId, obj)
        }
        prefs.edit().putString("chat_overrides_json", root.toString()).apply()
    }
}
