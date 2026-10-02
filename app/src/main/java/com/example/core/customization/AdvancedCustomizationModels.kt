package com.example.core.customization

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.json.JSONObject

// ----------------------------------------------------
// Home Screen Layout Presets
// ----------------------------------------------------
enum class HomeLayoutPreset(val id: String, val titleAr: String, val titleEn: String) {
    CLASSIC_LIST("classic", "قائمة كلاسيكية", "Classic List"),
    COMPACT("compact", "قائمة مدمجة سريعة", "Compact"),
    LARGE_CARDS("large_cards", "بطاقات عريضة غنية", "Large Cards"),
    MINIMAL("minimal", "مينيمال بسيط", "Minimal"),
    GRID("grid", "شبكة حديثة (2 Columns)", "Grid Modern"),
    FLOATING("floating", "بطاقات عائمة متباعدة", "Floating"),
    GLASS("glass", "بطاقات زجاجية شفافة", "Glassmorphic"),
    MODERN("modern", "عصري dev قياسي", "Modern dev")
}

data class HomeScreenDesignConfig(
    val layoutPreset: HomeLayoutPreset = HomeLayoutPreset.MODERN,
    val showHeaderLogo: Boolean = true,
    val isSearchFloating: Boolean = true,
    val cardRadiusDp: Int = 16,
    val cardSpacingDp: Int = 4,
    val cardElevationDp: Int = 1,
    val avatarSizeDp: Int = 52,
    val isAvatarCircular: Boolean = true,
    val showOnlineIndicatorBadge: Boolean = true,
    val showUnreadBadge: Boolean = true,
    val showPinMuteIcons: Boolean = true,
    val cardBorderWidthDp: Int = 0,
    val cardBorderColorHex: String = "#38BDF8"
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("layoutPreset", layoutPreset.id)
        put("showHeaderLogo", showHeaderLogo)
        put("isSearchFloating", isSearchFloating)
        put("cardRadiusDp", cardRadiusDp)
        put("cardSpacingDp", cardSpacingDp)
        put("cardElevationDp", cardElevationDp)
        put("avatarSizeDp", avatarSizeDp)
        put("isAvatarCircular", isAvatarCircular)
        put("showOnlineIndicatorBadge", showOnlineIndicatorBadge)
        put("showUnreadBadge", showUnreadBadge)
        put("showPinMuteIcons", showPinMuteIcons)
        put("cardBorderWidthDp", cardBorderWidthDp)
        put("cardBorderColorHex", cardBorderColorHex)
    }

    companion object {
        fun fromJson(json: JSONObject): HomeScreenDesignConfig {
            val presetStr = json.optString("layoutPreset", HomeLayoutPreset.MODERN.id)
            val preset = HomeLayoutPreset.entries.find { it.id == presetStr } ?: HomeLayoutPreset.MODERN
            return HomeScreenDesignConfig(
                layoutPreset = preset,
                showHeaderLogo = json.optBoolean("showHeaderLogo", true),
                isSearchFloating = json.optBoolean("isSearchFloating", true),
                cardRadiusDp = json.optInt("cardRadiusDp", 16),
                cardSpacingDp = json.optInt("cardSpacingDp", 4),
                cardElevationDp = json.optInt("cardElevationDp", 1),
                avatarSizeDp = json.optInt("avatarSizeDp", 52),
                isAvatarCircular = json.optBoolean("isAvatarCircular", true),
                showOnlineIndicatorBadge = json.optBoolean("showOnlineIndicatorBadge", true),
                showUnreadBadge = json.optBoolean("showUnreadBadge", true),
                showPinMuteIcons = json.optBoolean("showPinMuteIcons", true),
                cardBorderWidthDp = json.optInt("cardBorderWidthDp", 0),
                cardBorderColorHex = json.optString("cardBorderColorHex", "#38BDF8")
            )
        }
    }
}

// ----------------------------------------------------
// Wallpaper Editor Configuration
// ----------------------------------------------------
data class WallpaperEditorConfig(
    val brightness: Float = 1.0f,     // 0.2f..1.8f
    val contrast: Float = 1.0f,       // 0.5f..1.5f
    val saturation: Float = 1.0f,     // 0.0f..2.0f
    val blurRadiusDp: Float = 0f,     // 0f..24f
    val opacity: Float = 1.0f,        // 0.2f..1.0f
    val overlayDimHex: String = "#000000",
    val overlayDimAlpha: Float = 0.25f,
    val vignetteEnabled: Boolean = false,
    val zoomScale: Float = 1.0f,
    val rotationDeg: Float = 0f
)

// ----------------------------------------------------
// Video Wallpaper Configuration
// ----------------------------------------------------
data class VideoWallpaperConfig(
    val isEnabled: Boolean = false,
    val isLooping: Boolean = true,
    val isMuted: Boolean = true,
    val volume: Float = 0.0f,
    val playbackSpeed: Float = 1.0f,
    val opacity: Float = 0.85f,
    val blurRadiusDp: Float = 0f,
    val batterySaverMode: Boolean = true,
    val wifiOnlyPlayback: Boolean = true,
    val reduceMotionFallback: Boolean = true
)

// ----------------------------------------------------
// Conversation-Specific Theme Override
// ----------------------------------------------------
data class ConversationThemeOverride(
    val conversationId: String,
    val customWallpaperId: String? = null,
    val customThemeId: String? = null,
    val customBubbleRadiusDp: Int? = null,
    val customAccentColorHex: String? = null
)

// ----------------------------------------------------
// Color Harmony & Contrast Checker
// ----------------------------------------------------
object ColorHarmonyEngine {

    fun generateHarmonies(primary: Color): Map<String, List<Color>> {
        // Approximate HSV-like shifts in RGB space
        val comp = Color(
            red = 1f - primary.red,
            green = 1f - primary.green,
            blue = 1f - primary.blue,
            alpha = primary.alpha
        )

        val analogous1 = Color(
            red = (primary.red * 0.8f + primary.green * 0.2f).coerceIn(0f, 1f),
            green = (primary.green * 0.8f + primary.blue * 0.2f).coerceIn(0f, 1f),
            blue = (primary.blue * 0.8f + primary.red * 0.2f).coerceIn(0f, 1f)
        )

        val triadic = listOf(
            primary,
            Color(primary.green, primary.blue, primary.red),
            Color(primary.blue, primary.red, primary.green)
        )

        val mono = listOf(
            primary.copy(alpha = 0.3f),
            primary.copy(alpha = 0.6f),
            primary,
            Color(
                (primary.red * 0.7f).coerceIn(0f, 1f),
                (primary.green * 0.7f).coerceIn(0f, 1f),
                (primary.blue * 0.7f).coerceIn(0f, 1f)
            )
        )

        return mapOf(
            "Complementary" to listOf(primary, comp),
            "Analogous" to listOf(primary, analogous1),
            "Triadic" to triadic,
            "Monochromatic" to mono
        )
    }

    fun calculateContrastRatio(foreground: Color, background: Color): Float {
        val lum1 = calculateLuminance(foreground)
        val lum2 = calculateLuminance(background)
        val brightest = maxOf(lum1, lum2)
        val darkest = minOf(lum1, lum2)
        return (brightest + 0.05f) / (darkest + 0.05f)
    }

    private fun calculateLuminance(color: Color): Float {
        return 0.2126f * color.red + 0.7152f * color.green + 0.0722f * color.blue
    }
}
