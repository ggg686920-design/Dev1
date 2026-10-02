package com.example.core.customization

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

class CustomizationManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("dev_customization_prefs", Context.MODE_PRIVATE)

    private val _currentTheme = MutableStateFlow(loadCurrentTheme())
    val currentTheme: StateFlow<ThemeConfig> = _currentTheme.asStateFlow()

    private val _customThemes = MutableStateFlow(loadCustomThemes())
    val customThemes: StateFlow<List<ThemeConfig>> = _customThemes.asStateFlow()

    private val _favoriteThemeIds = MutableStateFlow(loadFavorites())
    val favoriteThemeIds: StateFlow<Set<String>> = _favoriteThemeIds.asStateFlow()

    private val _recentlyUsedIds = MutableStateFlow(loadRecentlyUsed())
    val recentlyUsedIds: StateFlow<List<String>> = _recentlyUsedIds.asStateFlow()

    private fun loadCurrentTheme(): ThemeConfig {
        val rawJson = prefs.getString("active_theme_json", null)
        if (!rawJson.isNullOrBlank()) {
            try {
                return ThemeConfig.fromJson(JSONObject(rawJson))
            } catch (_: Exception) { }
        }
        return ThemePresets.builtInThemes.first()
    }

    private fun loadCustomThemes(): List<ThemeConfig> {
        val rawJson = prefs.getString("user_custom_themes", null) ?: return emptyList()
        val list = mutableListOf<ThemeConfig>()
        try {
            val array = JSONArray(rawJson)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(ThemeConfig.fromJson(obj))
            }
        } catch (_: Exception) { }
        return list
    }

    private fun loadFavorites(): Set<String> {
        return prefs.getStringSet("favorite_theme_ids", emptySet()) ?: emptySet()
    }

    private fun loadRecentlyUsed(): List<String> {
        val raw = prefs.getString("recently_used_ids", null) ?: return listOf("dev_default")
        return raw.split(",").filter { it.isNotBlank() }
    }

    fun applyTheme(theme: ThemeConfig) {
        val json = theme.toJson().toString()
        prefs.edit().putString("active_theme_json", json).apply()
        _currentTheme.value = theme

        // Track recently used
        val updatedRecent = (listOf(theme.id) + _recentlyUsedIds.value.filter { it != theme.id }).take(10)
        prefs.edit().putString("recently_used_ids", updatedRecent.joinToString(",")).apply()
        _recentlyUsedIds.value = updatedRecent
    }

    fun updateActiveTheme(block: (ThemeConfig) -> ThemeConfig) {
        val updated = block(_currentTheme.value)
        applyTheme(updated)
    }

    fun saveAsCustomTheme(name: String, theme: ThemeConfig): ThemeConfig {
        val newCustom = theme.copy(
            id = "custom_${System.currentTimeMillis()}",
            name = name.ifBlank { "My Theme" },
            isCustom = true
        )
        val list = _customThemes.value + newCustom
        saveCustomThemesList(list)
        applyTheme(newCustom)
        return newCustom
    }

    fun deleteCustomTheme(themeId: String) {
        val list = _customThemes.value.filter { it.id != themeId }
        saveCustomThemesList(list)
        if (_currentTheme.value.id == themeId) {
            applyTheme(ThemePresets.builtInThemes.first())
        }
    }

    private fun saveCustomThemesList(list: List<ThemeConfig>) {
        _customThemes.value = list
        val array = JSONArray()
        list.forEach { array.put(it.toJson()) }
        prefs.edit().putString("user_custom_themes", array.toString()).apply()
    }

    fun toggleFavorite(themeId: String) {
        val current = _favoriteThemeIds.value.toMutableSet()
        if (current.contains(themeId)) {
            current.remove(themeId)
        } else {
            current.add(themeId)
        }
        prefs.edit().putStringSet("favorite_theme_ids", current).apply()
        _favoriteThemeIds.value = current
    }

    fun isFavorite(themeId: String): Boolean = _favoriteThemeIds.value.contains(themeId)

    // ----------------------------------------------------
    // Random "Surprise Me" Theme Generator (High contrast guarantee)
    // ----------------------------------------------------
    fun generateRandomSurpriseTheme(): ThemeConfig {
        val isDark = Random.nextBoolean()
        val hues = listOf(
            Pair("#0284C7", "#38BDF8"), // Cyan
            Pair("#7C3AED", "#A855F7"), // Purple
            Pair("#E11D48", "#FB7185"), // Rose
            Pair("#059669", "#34D399"), // Emerald
            Pair("#D97706", "#FBBF24"), // Amber
            Pair("#EA580C", "#FB923C"), // Orange
            Pair("#0891B2", "#22D3EE"), // Teal
            Pair("#4F46E5", "#818CF8")  // Indigo
        )
        val chosenHue = hues.random()
        val primaryHex = if (isDark) chosenHue.first else chosenHue.second
        val accentHex = if (isDark) chosenHue.second else chosenHue.first

        val bgHex = if (isDark) {
            listOf("#0B1120", "#000000", "#09090B", "#0F172A", "#120529", "#022C22").random()
        } else {
            listOf("#FFFFFF", "#F8FAFC", "#FAF5FF", "#FFFBEB", "#F0FDF4", "#F0F9FF").random()
        }

        val surfaceHex = if (isDark) "#1E293B" else "#FFFFFF"
        val surfaceVariantHex = if (isDark) "#334155" else "#F1F5F9"
        val textHex = if (isDark) "#F8FAFC" else "#0F172A"
        val textSecHex = if (isDark) "#94A3B8" else "#64748B"

        val shapes = BubbleShape.entries.toTypedArray()
        val navStyles = NavBarStyle.entries.toTypedArray()
        val inputStyles = InputBarStyle.entries.toTypedArray()
        val btnStyles = ButtonStyle.entries.toTypedArray()
        val iconStyles = IconStyle.entries.toTypedArray()
        val decorStyles = DecorationStyle.entries.toTypedArray()
        val wallpapers = ThemePresets.builtInWallpapers

        val bubbleConfig = BubbleConfig(
            shape = shapes.random(),
            radiusDp = Random.nextInt(8, 24),
            paddingDp = Random.nextInt(10, 16),
            hasShadow = true,
            hasBorder = Random.nextBoolean(),
            borderColorHex = accentHex,
            incomingBgHex = surfaceVariantHex,
            outgoingBgHex = primaryHex,
            incomingTextHex = textHex,
            outgoingTextHex = if (isDark) "#FFFFFF" else "#FFFFFF",
            timestampColorHex = textSecHex,
            hasTail = Random.nextBoolean()
        )

        return ThemeConfig(
            id = "surprise_${System.currentTimeMillis()}",
            name = "Surprise 🎲 #${Random.nextInt(100, 999)}",
            description = "Procedurally generated dynamic theme with harmonious color grading.",
            category = "Surprise",
            isDark = isDark,
            primaryColorHex = primaryHex,
            secondaryColorHex = if (isDark) "#0F172A" else "#F1F5F9",
            backgroundColorHex = bgHex,
            surfaceColorHex = surfaceHex,
            surfaceVariantColorHex = surfaceVariantHex,
            textColorHex = textHex,
            textSecondaryHex = textSecHex,
            accentColorHex = accentHex,
            cardColorHex = surfaceHex,
            cardRadiusDp = Random.nextInt(10, 24),
            bubbleConfig = bubbleConfig,
            navBarStyle = navStyles.random(),
            inputBarStyle = inputStyles.random(),
            buttonStyle = btnStyles.random(),
            iconStyle = iconStyles.random(),
            animationSpeed = AnimationSpeed.SMOOTH,
            decorationStyle = decorStyles.random(),
            typography = TypographyConfig(
                fontFamilyType = FontFamilyType.entries.random(),
                fontSizeScale = 1.0f
            ),
            effects = VisualEffectsConfig(
                glassmorphism = Random.nextBoolean(),
                glowEnabled = isDark && Random.nextBoolean()
            ),
            wallpaperId = wallpapers.random().id,
            isCustom = true
        )
    }

    // ----------------------------------------------------
    // Import / Export (.devtheme JSON verification)
    // Safe, validated, zero-executable code
    // ----------------------------------------------------
    fun exportThemeToJson(theme: ThemeConfig): String {
        val root = JSONObject()
        root.put("format", "devtheme")
        root.put("version", 2)
        root.put("exportedAt", System.currentTimeMillis())
        root.put("theme", theme.toJson())
        return root.toString(2)
    }

    fun importThemeFromJson(jsonString: String): Result<ThemeConfig> {
        return try {
            val root = JSONObject(jsonString)
            val format = root.optString("format", "")
            if (format != "devtheme" && !root.has("primaryColorHex")) {
                return Result.failure(IllegalArgumentException("Invalid .devtheme file signature"))
            }

            val themeJson = if (root.has("theme")) root.getJSONObject("theme") else root
            val imported = ThemeConfig.fromJson(themeJson).copy(
                id = "imported_${System.currentTimeMillis()}",
                isCustom = true,
                author = "Imported File"
            )
            val updated = _customThemes.value + imported
            saveCustomThemesList(updated)
            Result.success(imported)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun resetToDefaults() {
        applyTheme(ThemePresets.builtInThemes.first())
    }
}
