package com.example.core.network

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SupabaseConfig(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("raseel_supabase_prefs", Context.MODE_PRIVATE)

    private val _urlFlow = MutableStateFlow(getUrl())
    val urlFlow: StateFlow<String> = _urlFlow.asStateFlow()

    private val _anonKeyFlow = MutableStateFlow(getAnonKey())
    val anonKeyFlow: StateFlow<String> = _anonKeyFlow.asStateFlow()

    fun getUrl(): String {
        val saved = prefs.getString(KEY_URL, null)
        if (!saved.isNullOrBlank()) return saved
        val buildConfigUrl = runCatching { BuildConfig::class.java.getField("SUPABASE_URL").get(null) as? String }.getOrNull()
        return if (!buildConfigUrl.isNullOrBlank() && !buildConfigUrl.contains("placeholder")) {
            buildConfigUrl
        } else {
            "https://placeholder-project.supabase.co"
        }
    }

    fun getAnonKey(): String {
        val saved = prefs.getString(KEY_KEY, null)
        if (!saved.isNullOrBlank()) return saved
        val buildConfigKey = runCatching { BuildConfig::class.java.getField("SUPABASE_ANON_KEY").get(null) as? String }.getOrNull()
        return if (!buildConfigKey.isNullOrBlank() && !buildConfigKey.contains("placeholder")) {
            buildConfigKey
        } else {
            "placeholder-anon-key"
        }
    }

    fun updateConfig(url: String, key: String) {
        val cleanUrl = url.trim().removeSuffix("/")
        val cleanKey = key.trim()
        prefs.edit()
            .putString(KEY_URL, cleanUrl)
            .putString(KEY_KEY, cleanKey)
            .apply()
        _urlFlow.value = cleanUrl
        _anonKeyFlow.value = cleanKey
    }

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)
    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)
    fun getCurrentUserId(): String? = prefs.getString(KEY_USER_ID, null)

    fun saveSession(accessToken: String, refreshToken: String, userId: String) {
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .putString(KEY_USER_ID, userId)
            .apply()
    }

    fun clearSession() {
        prefs.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .remove(KEY_USER_ID)
            .apply()
    }

    fun isConfigured(): Boolean {
        val url = getUrl()
        val key = getAnonKey()
        return url.startsWith("https://") && !url.contains("placeholder") && key.length > 20 && !key.contains("placeholder")
    }

    fun isDemoMode(): Boolean {
        return getCurrentUserId() == DEMO_USER_ID || !isConfigured()
    }

    companion object {
        const val DEMO_USER_ID = "user-214608-test"
        private const val KEY_URL = "supabase_url"
        private const val KEY_KEY = "supabase_key"
        private const val KEY_ACCESS_TOKEN = "supabase_access_token"
        private const val KEY_REFRESH_TOKEN = "supabase_refresh_token"
        private const val KEY_USER_ID = "supabase_user_id"
    }
}
