package com.example.core.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class AccentColor(val displayNameAr: String, val displayNameEn: String, val lightColor: Color, val darkColor: Color) {
    TEAL("زمردي / افتراضي", "Teal / Default", Color(0xFF0F766E), Color(0xFF14B8A6)),
    BLUE("أزرق محيطي", "Ocean Blue", Color(0xFF1D4ED8), Color(0xFF3B82F6)),
    PURPLE("بنفسجي ملكي", "Royal Purple", Color(0xFF6D28D9), Color(0xFF8B5CF6)),
    GREEN("أخضر ربيعي", "Spring Green", Color(0xFF047857), Color(0xFF10B981)),
    RED("أحمر ياقوتي", "Crimson Red", Color(0xFFBE123C), Color(0xFFF43F5E)),
    ORANGE("برتقالي غروب", "Sunset Orange", Color(0xFFC2410C), Color(0xFFF97316)),
    PINK("وردي زاهي", "Rose Pink", Color(0xFFBE185D), Color(0xFFEC4899)),
    CYAN("سماوي بحري", "Marine Cyan", Color(0xFF0E7490), Color(0xFF06B6D4))
}

enum class WallpaperPreset(val id: String, val titleAr: String, val titleEn: String, val backgroundHex: String, val isDark: Boolean) {
    DEFAULT("default", "افتراضي ناعم", "Default Soft", "#F8FAFC", false),
    SLATE("slate", "رمادي هادئ", "Slate Calm", "#F1F5F9", false),
    WARM_SAND("warm_sand", "رمال دافئة", "Warm Sand", "#FDF6EC", false),
    OCEAN_BREEZE("ocean_breeze", "نسيم المحيط", "Ocean Breeze", "#EFF6FF", false),
    MINT_FRESH("mint_fresh", "نعناع منعش", "Mint Fresh", "#ECFDF5", false),
    MIDNIGHT("midnight", "ليل حالك", "Midnight Navy", "#0F172A", true),
    DARK_CHARCOAL("dark_charcoal", "فحم داكن", "Dark Charcoal", "#18181B", true)
}

enum class BubbleRadius(val radiusDp: Int, val titleAr: String, val titleEn: String) {
    SMALL(6, "حواف مدمجة (6dp)", "Compact (6dp)"),
    MEDIUM(16, "حواف متناسقة (16dp)", "Balanced (16dp)"),
    ROUND(24, "حواف دائرية (24dp)", "Extra Round (24dp)")
}

enum class FontSizeScale(val scale: Float, val titleAr: String, val titleEn: String) {
    SMALL(0.85f, "صغير (85%)", "Small (85%)"),
    MEDIUM(1.0f, "متوسط / عادي (100%)", "Medium / Normal (100%)"),
    LARGE(1.15f, "كبير (115%)", "Large (115%)"),
    EXTRA_LARGE(1.30f, "كبير جداً (130%)", "Extra Large (130%)")
}

data class PrivacySettings(
    val lastSeenVisibility: String = "EVERYONE", // EVERYONE, CONTACTS, NOBODY
    val photoVisibility: String = "EVERYONE",
    val readReceiptsEnabled: Boolean = true,
    val onlineStatusVisibility: String = "EVERYONE"
)

data class NotificationSettings(
    val enabled: Boolean = true,
    val messageNotifications: Boolean = true,
    val groupNotifications: Boolean = true,
    val sound: Boolean = true,
    val vibration: Boolean = true,
    val showPreview: Boolean = true
)

data class DataUsageSettings(
    val autoDownloadWifiPhotos: Boolean = true,
    val autoDownloadWifiAudio: Boolean = true,
    val autoDownloadWifiFiles: Boolean = false,
    val autoDownloadMobilePhotos: Boolean = true,
    val autoDownloadMobileAudio: Boolean = false,
    val autoDownloadMobileFiles: Boolean = false
)

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val accentColor: AccentColor = AccentColor.TEAL,
    val wallpaperPreset: WallpaperPreset = WallpaperPreset.DEFAULT,
    val bubbleRadius: BubbleRadius = BubbleRadius.MEDIUM,
    val fontSizeScale: FontSizeScale = FontSizeScale.MEDIUM,
    val isArabic: Boolean = true,
    val privacy: PrivacySettings = PrivacySettings(),
    val notifications: NotificationSettings = NotificationSettings(),
    val dataUsage: DataUsageSettings = DataUsageSettings(),
    val isSimulatedOffline: Boolean = false,
    val cachedStorageBytes: Long = 48_500_000L
)

class AppSettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("raseel_app_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private fun loadSettings(): AppSettings {
        val themeModeStr = prefs.getString("theme_mode", ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        val accentColorStr = prefs.getString("accent_color", AccentColor.TEAL.name) ?: AccentColor.TEAL.name
        val wallpaperStr = prefs.getString("wallpaper_preset", WallpaperPreset.DEFAULT.id) ?: WallpaperPreset.DEFAULT.id
        val bubbleRadiusStr = prefs.getString("bubble_radius", BubbleRadius.MEDIUM.name) ?: BubbleRadius.MEDIUM.name
        val fontScaleStr = prefs.getString("font_scale", FontSizeScale.MEDIUM.name) ?: FontSizeScale.MEDIUM.name
        val isArabic = prefs.getBoolean("is_arabic", true)

        val privacy = PrivacySettings(
            lastSeenVisibility = prefs.getString("privacy_last_seen", "EVERYONE") ?: "EVERYONE",
            photoVisibility = prefs.getString("privacy_photo", "EVERYONE") ?: "EVERYONE",
            readReceiptsEnabled = prefs.getBoolean("privacy_read_receipts", true),
            onlineStatusVisibility = prefs.getString("privacy_online", "EVERYONE") ?: "EVERYONE"
        )

        val notifications = NotificationSettings(
            enabled = prefs.getBoolean("notif_enabled", true),
            messageNotifications = prefs.getBoolean("notif_messages", true),
            groupNotifications = prefs.getBoolean("notif_groups", true),
            sound = prefs.getBoolean("notif_sound", true),
            vibration = prefs.getBoolean("notif_vibration", true),
            showPreview = prefs.getBoolean("notif_preview", true)
        )

        val dataUsage = DataUsageSettings(
            autoDownloadWifiPhotos = prefs.getBoolean("data_wifi_photos", true),
            autoDownloadWifiAudio = prefs.getBoolean("data_wifi_audio", true),
            autoDownloadWifiFiles = prefs.getBoolean("data_wifi_files", false),
            autoDownloadMobilePhotos = prefs.getBoolean("data_mobile_photos", true),
            autoDownloadMobileAudio = prefs.getBoolean("data_mobile_audio", false),
            autoDownloadMobileFiles = prefs.getBoolean("data_mobile_files", false)
        )

        val themeMode = runCatching { ThemeMode.valueOf(themeModeStr) }.getOrDefault(ThemeMode.SYSTEM)
        val accentColor = runCatching { AccentColor.valueOf(accentColorStr) }.getOrDefault(AccentColor.TEAL)
        val wallpaper = WallpaperPreset.entries.find { it.id == wallpaperStr } ?: WallpaperPreset.DEFAULT
        val bubbleRadius = runCatching { BubbleRadius.valueOf(bubbleRadiusStr) }.getOrDefault(BubbleRadius.MEDIUM)
        val fontScale = runCatching { FontSizeScale.valueOf(fontScaleStr) }.getOrDefault(FontSizeScale.MEDIUM)
        val cachedBytes = prefs.getLong("cached_storage_bytes", 48_500_000L)

        return AppSettings(
            themeMode = themeMode,
            accentColor = accentColor,
            wallpaperPreset = wallpaper,
            bubbleRadius = bubbleRadius,
            fontSizeScale = fontScale,
            isArabic = isArabic,
            privacy = privacy,
            notifications = notifications,
            dataUsage = dataUsage,
            isSimulatedOffline = false,
            cachedStorageBytes = cachedBytes
        )
    }

    fun updateThemeMode(mode: ThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
        _settings.value = _settings.value.copy(themeMode = mode)
    }

    fun updateAccentColor(accent: AccentColor) {
        prefs.edit().putString("accent_color", accent.name).apply()
        _settings.value = _settings.value.copy(accentColor = accent)
    }

    fun updateWallpaperPreset(preset: WallpaperPreset) {
        prefs.edit().putString("wallpaper_preset", preset.id).apply()
        _settings.value = _settings.value.copy(wallpaperPreset = preset)
    }

    fun updateBubbleRadius(radius: BubbleRadius) {
        prefs.edit().putString("bubble_radius", radius.name).apply()
        _settings.value = _settings.value.copy(bubbleRadius = radius)
    }

    fun updateFontSizeScale(scale: FontSizeScale) {
        prefs.edit().putString("font_scale", scale.name).apply()
        _settings.value = _settings.value.copy(fontSizeScale = scale)
    }

    fun updateLanguage(isArabic: Boolean) {
        prefs.edit().putBoolean("is_arabic", isArabic).apply()
        _settings.value = _settings.value.copy(isArabic = isArabic)
    }

    fun updatePrivacy(privacy: PrivacySettings) {
        prefs.edit()
            .putString("privacy_last_seen", privacy.lastSeenVisibility)
            .putString("privacy_photo", privacy.photoVisibility)
            .putBoolean("privacy_read_receipts", privacy.readReceiptsEnabled)
            .putString("privacy_online", privacy.onlineStatusVisibility)
            .apply()
        _settings.value = _settings.value.copy(privacy = privacy)
    }

    fun updateNotifications(notif: NotificationSettings) {
        prefs.edit()
            .putBoolean("notif_enabled", notif.enabled)
            .putBoolean("notif_messages", notif.messageNotifications)
            .putBoolean("notif_groups", notif.groupNotifications)
            .putBoolean("notif_sound", notif.sound)
            .putBoolean("notif_vibration", notif.vibration)
            .putBoolean("notif_preview", notif.showPreview)
            .apply()
        _settings.value = _settings.value.copy(notifications = notif)
    }

    fun updateDataUsage(data: DataUsageSettings) {
        prefs.edit()
            .putBoolean("data_wifi_photos", data.autoDownloadWifiPhotos)
            .putBoolean("data_wifi_audio", data.autoDownloadWifiAudio)
            .putBoolean("data_wifi_files", data.autoDownloadWifiFiles)
            .putBoolean("data_mobile_photos", data.autoDownloadMobilePhotos)
            .putBoolean("data_mobile_audio", data.autoDownloadMobileAudio)
            .putBoolean("data_mobile_files", data.autoDownloadMobileFiles)
            .apply()
        _settings.value = _settings.value.copy(dataUsage = data)
    }

    fun toggleSimulatedOffline() {
        val next = !_settings.value.isSimulatedOffline
        _settings.value = _settings.value.copy(isSimulatedOffline = next)
    }

    fun clearCache(): Long {
        val freed = _settings.value.cachedStorageBytes
        prefs.edit().putLong("cached_storage_bytes", 0L).apply()
        _settings.value = _settings.value.copy(cachedStorageBytes = 0L)
        return freed
    }
}
