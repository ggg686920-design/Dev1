package com.example.core.assets

import org.json.JSONObject

enum class AssetType(val id: String, val titleAr: String, val titleEn: String) {
    THEME("theme", "ثيمات متكاملة", "Themes"),
    WALLPAPER("wallpaper", "خلفيات الشاشة", "Wallpapers"),
    ANIMATED_WALLPAPER("animated_wallpaper", "خلفيات متحركة وفيديو", "Animated Wallpapers"),
    ICON_PACK("icon_pack", "حزم الأيقونات", "Icon Packs"),
    DECORATION("decoration", "عناصر الزخرفة والإطارات", "Decorations"),
    FONT("font", "الخطوط والطباعة", "Fonts"),
    STICKER_PACK("sticker_pack", "حزم الملصقات", "Sticker Packs"),
    REACTION_PACK("reaction_pack", "حزم التفاعلات", "Reaction Packs"),
    UI_PRESET("ui_preset", "قوالب الواجهة", "UI Presets")
}

enum class DownloadStatus {
    NOT_DOWNLOADED,
    DOWNLOADING,
    PAUSED,
    DOWNLOADED,
    UPDATE_AVAILABLE,
    FAILED
}

data class AssetLicense(
    val author: String = "dev Community",
    val licenseName: String = "MIT / Open-Source Free Commercial",
    val sourceUrl: String = "https://github.com/twbs/icons",
    val isAttributionRequired: Boolean = false,
    val version: String = "1.13.1"
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("author", author)
        put("licenseName", licenseName)
        put("sourceUrl", sourceUrl)
        put("isAttributionRequired", isAttributionRequired)
        put("version", version)
    }

    companion object {
        fun fromJson(json: JSONObject): AssetLicense = AssetLicense(
            author = json.optString("author", "dev Community"),
            licenseName = json.optString("licenseName", "MIT / Free"),
            sourceUrl = json.optString("sourceUrl", "https://dev.local"),
            isAttributionRequired = json.optBoolean("isAttributionRequired", false),
            version = json.optString("version", "1.0")
        )
    }
}

data class AssetItem(
    val id: String,
    val name: String,
    val description: String,
    val type: AssetType,
    val category: String,
    val author: String,
    val version: String = "1.0",
    val sizeBytes: Long = 150_000L,
    val downloadCount: Int = 1200,
    val rating: Float = 4.8f,
    val isFeatured: Boolean = false,
    val isTrending: Boolean = false,
    val isPopular: Boolean = false,
    val isRecommended: Boolean = false,
    val isFree: Boolean = true,
    val previewPrimaryHex: String = "#0284C7",
    val previewBgHex: String = "#0B1120",
    val previewAccentHex: String = "#38BDF8",
    val dataJson: String = "",
    val license: AssetLicense = AssetLicense(author = author),
    val downloadStatus: DownloadStatus = DownloadStatus.NOT_DOWNLOADED,
    val downloadProgress: Float = 0f,
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("name", name)
        put("description", description)
        put("type", type.id)
        put("category", category)
        put("author", author)
        put("version", version)
        put("sizeBytes", sizeBytes)
        put("downloadCount", downloadCount)
        put("rating", rating.toDouble())
        put("isFeatured", isFeatured)
        put("isTrending", isTrending)
        put("isPopular", isPopular)
        put("isRecommended", isRecommended)
        put("isFree", isFree)
        put("previewPrimaryHex", previewPrimaryHex)
        put("previewBgHex", previewBgHex)
        put("previewAccentHex", previewAccentHex)
        put("dataJson", dataJson)
        put("license", license.toJson())
        put("downloadStatus", downloadStatus.name)
        put("updatedAt", updatedAt)
    }

    companion object {
        fun fromJson(json: JSONObject): AssetItem {
            val typeStr = json.optString("type", AssetType.THEME.id)
            val type = AssetType.entries.find { it.id == typeStr } ?: AssetType.THEME
            val statusStr = json.optString("downloadStatus", DownloadStatus.NOT_DOWNLOADED.name)
            val status = runCatching { DownloadStatus.valueOf(statusStr) }.getOrDefault(DownloadStatus.NOT_DOWNLOADED)
            val licenseObj = json.optJSONObject("license") ?: JSONObject()

            return AssetItem(
                id = json.optString("id", "asset_${System.currentTimeMillis()}"),
                name = json.optString("name", "Asset"),
                description = json.optString("description", ""),
                type = type,
                category = json.optString("category", "General"),
                author = json.optString("author", "dev Creator"),
                version = json.optString("version", "1.0"),
                sizeBytes = json.optLong("sizeBytes", 150_000L),
                downloadCount = json.optInt("downloadCount", 1200),
                rating = json.optDouble("rating", 4.8).toFloat(),
                isFeatured = json.optBoolean("isFeatured", false),
                isTrending = json.optBoolean("isTrending", false),
                isPopular = json.optBoolean("isPopular", false),
                isRecommended = json.optBoolean("isRecommended", false),
                isFree = json.optBoolean("isFree", true),
                previewPrimaryHex = json.optString("previewPrimaryHex", "#0284C7"),
                previewBgHex = json.optString("previewBgHex", "#0B1120"),
                previewAccentHex = json.optString("previewAccentHex", "#38BDF8"),
                dataJson = json.optString("dataJson", ""),
                license = AssetLicense.fromJson(licenseObj),
                downloadStatus = status,
                updatedAt = json.optLong("updatedAt", System.currentTimeMillis())
            )
        }
    }
}

data class StorageCacheInfo(
    val themeCacheBytes: Long = 12_400_000L,
    val wallpaperCacheBytes: Long = 34_800_000L,
    val iconCacheBytes: Long = 8_200_000L,
    val fontCacheBytes: Long = 6_500_000L,
    val videoWallpaperCacheBytes: Long = 45_000_000L
) {
    val totalBytes: Long
        get() = themeCacheBytes + wallpaperCacheBytes + iconCacheBytes + fontCacheBytes + videoWallpaperCacheBytes
}
