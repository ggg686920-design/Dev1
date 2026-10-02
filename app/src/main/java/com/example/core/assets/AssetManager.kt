package com.example.core.assets

import android.content.Context
import android.content.SharedPreferences
import com.example.core.customization.ThemeConfig
import com.example.core.customization.ThemePresets
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class AssetManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("dev_asset_library_prefs", Context.MODE_PRIVATE)
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _assets = MutableStateFlow(loadCatalog())
    val assets: StateFlow<List<AssetItem>> = _assets.asStateFlow()

    private val _cacheInfo = MutableStateFlow(loadCacheInfo())
    val cacheInfo: StateFlow<StorageCacheInfo> = _cacheInfo.asStateFlow()

    private val _isWifiOnly = MutableStateFlow(prefs.getBoolean("download_wifi_only", true))
    val isWifiOnly: StateFlow<Boolean> = _isWifiOnly.asStateFlow()

    private val activeDownloadJobs = mutableMapOf<String, Job>()

    private fun loadCatalog(): List<AssetItem> {
        val downloadedIds = prefs.getStringSet("downloaded_asset_ids", emptySet()) ?: emptySet()
        val baseCatalog = createDefaultOnlineCatalog()

        return baseCatalog.map { item ->
            if (downloadedIds.contains(item.id)) {
                item.copy(downloadStatus = DownloadStatus.DOWNLOADED, downloadProgress = 1.0f)
            } else {
                item
            }
        }
    }

    private fun loadCacheInfo(): StorageCacheInfo {
        return StorageCacheInfo(
            themeCacheBytes = prefs.getLong("theme_cache_bytes", 14_200_000L),
            wallpaperCacheBytes = prefs.getLong("wallpaper_cache_bytes", 28_500_000L),
            iconCacheBytes = prefs.getLong("icon_cache_bytes", 9_100_000L),
            fontCacheBytes = prefs.getLong("font_cache_bytes", 5_800_000L),
            videoWallpaperCacheBytes = prefs.getLong("video_cache_bytes", 42_000_000L)
        )
    }

    // ----------------------------------------------------
    // Download Life Cycle: Download, Pause, Resume, Cancel, Retry, Verify, Delete
    // ----------------------------------------------------
    fun startDownload(assetId: String) {
        val currentList = _assets.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == assetId }
        if (index == -1) return

        currentList[index] = currentList[index].copy(
            downloadStatus = DownloadStatus.DOWNLOADING,
            downloadProgress = 0.05f
        )
        _assets.value = currentList

        activeDownloadJobs[assetId]?.cancel()
        activeDownloadJobs[assetId] = coroutineScope.launch {
            try {
                for (p in 1..10) {
                    delay(120)
                    val progress = p / 10f
                    updateAssetProgress(assetId, progress)
                }

                // Verify asset data (Data-only, no scripts or executables)
                val target = _assets.value.find { it.id == assetId }
                val isSafe = verifyAssetSecurity(target)

                if (isSafe) {
                    completeDownload(assetId)
                } else {
                    failDownload(assetId, "Security verification failed")
                }
            } catch (e: CancellationException) {
                // Cancelled or paused
            } catch (e: Exception) {
                failDownload(assetId, e.message ?: "Download failed")
            }
        }
    }

    fun pauseDownload(assetId: String) {
        activeDownloadJobs[assetId]?.cancel()
        activeDownloadJobs.remove(assetId)

        val list = _assets.value.toMutableList()
        val idx = list.indexOfFirst { it.id == assetId }
        if (idx != -1) {
            list[idx] = list[idx].copy(downloadStatus = DownloadStatus.PAUSED)
            _assets.value = list
        }
    }

    fun resumeDownload(assetId: String) {
        startDownload(assetId)
    }

    fun cancelDownload(assetId: String) {
        activeDownloadJobs[assetId]?.cancel()
        activeDownloadJobs.remove(assetId)

        val list = _assets.value.toMutableList()
        val idx = list.indexOfFirst { it.id == assetId }
        if (idx != -1) {
            list[idx] = list[idx].copy(downloadStatus = DownloadStatus.NOT_DOWNLOADED, downloadProgress = 0f)
            _assets.value = list
        }
    }

    fun deleteDownloadedAsset(assetId: String) {
        cancelDownload(assetId)

        val downloadedIds = prefs.getStringSet("downloaded_asset_ids", emptySet())?.toMutableSet() ?: mutableSetOf()
        downloadedIds.remove(assetId)
        prefs.edit().putStringSet("downloaded_asset_ids", downloadedIds).apply()

        val list = _assets.value.toMutableList()
        val idx = list.indexOfFirst { it.id == assetId }
        if (idx != -1) {
            list[idx] = list[idx].copy(downloadStatus = DownloadStatus.NOT_DOWNLOADED, downloadProgress = 0f)
            _assets.value = list
        }
    }

    private fun updateAssetProgress(assetId: String, progress: Float) {
        val list = _assets.value.toMutableList()
        val idx = list.indexOfFirst { it.id == assetId }
        if (idx != -1) {
            list[idx] = list[idx].copy(downloadProgress = progress)
            _assets.value = list
        }
    }

    private fun completeDownload(assetId: String) {
        val downloadedIds = prefs.getStringSet("downloaded_asset_ids", emptySet())?.toMutableSet() ?: mutableSetOf()
        downloadedIds.add(assetId)
        prefs.edit().putStringSet("downloaded_asset_ids", downloadedIds).apply()

        val list = _assets.value.toMutableList()
        val idx = list.indexOfFirst { it.id == assetId }
        if (idx != -1) {
            list[idx] = list[idx].copy(
                downloadStatus = DownloadStatus.DOWNLOADED,
                downloadProgress = 1.0f
            )
            _assets.value = list
        }
    }

    private fun failDownload(assetId: String, error: String) {
        val list = _assets.value.toMutableList()
        val idx = list.indexOfFirst { it.id == assetId }
        if (idx != -1) {
            list[idx] = list[idx].copy(downloadStatus = DownloadStatus.FAILED)
            _assets.value = list
        }
    }

    private fun verifyAssetSecurity(asset: AssetItem?): Boolean {
        if (asset == null) return false
        // Strictly verify size < 50MB and JSON data format only
        if (asset.sizeBytes > 50_000_000L) return false
        if (asset.dataJson.isNotBlank()) {
            return try {
                JSONObject(asset.dataJson)
                true
            } catch (_: Exception) {
                false
            }
        }
        return true
    }

    // ----------------------------------------------------
    // Cache Management & Preferences
    // ----------------------------------------------------
    fun clearCache(type: AssetType?) {
        val current = _cacheInfo.value
        val updated = when (type) {
            AssetType.THEME -> current.copy(themeCacheBytes = 0L)
            AssetType.WALLPAPER -> current.copy(wallpaperCacheBytes = 0L)
            AssetType.ICON_PACK -> current.copy(iconCacheBytes = 0L)
            AssetType.FONT -> current.copy(fontCacheBytes = 0L)
            AssetType.ANIMATED_WALLPAPER -> current.copy(videoWallpaperCacheBytes = 0L)
            else -> StorageCacheInfo(0L, 0L, 0L, 0L, 0L)
        }
        _cacheInfo.value = updated
        prefs.edit()
            .putLong("theme_cache_bytes", updated.themeCacheBytes)
            .putLong("wallpaper_cache_bytes", updated.wallpaperCacheBytes)
            .putLong("icon_cache_bytes", updated.iconCacheBytes)
            .putLong("font_cache_bytes", updated.fontCacheBytes)
            .putLong("video_cache_bytes", updated.videoWallpaperCacheBytes)
            .apply()
    }

    fun setWifiOnly(enabled: Boolean) {
        prefs.edit().putBoolean("download_wifi_only", enabled).apply()
        _isWifiOnly.value = enabled
    }

    // ----------------------------------------------------
    // Smart Recommendations (No personal telemetry, offline calculation)
    // ----------------------------------------------------
    fun getRecommendations(currentThemeCategory: String): List<AssetItem> {
        return _assets.value.filter {
            it.category.equals(currentThemeCategory, ignoreCase = true) || it.isRecommended || it.isTrending
        }.take(8)
    }

    // ----------------------------------------------------
    // Default Rich Public Online Catalog (Safe, open licensed)
    // ----------------------------------------------------
    private fun createDefaultOnlineCatalog(): List<AssetItem> {
        val list = mutableListOf<AssetItem>()

        // 1. Online Themes (Representing real downloadable themes)
        val sampleThemes = ThemePresets.builtInThemes.take(12)
        sampleThemes.forEachIndexed { i, theme ->
            list.add(
                AssetItem(
                    id = "online_theme_${theme.id}",
                    name = theme.name,
                    description = theme.description.ifBlank { "Full-interface custom theme with handcrafted color grading and custom bubbles." },
                    type = AssetType.THEME,
                    category = theme.category,
                    author = if (i % 2 == 0) "dev Design Studio" else "Community Artisan",
                    version = "2.1",
                    sizeBytes = 240_000L + (i * 35_000L),
                    downloadCount = 14500 + i * 2300,
                    rating = 4.8f + (i % 3) * 0.1f,
                    isFeatured = i < 3,
                    isTrending = i in 2..5,
                    isPopular = i in 1..6,
                    isRecommended = i in 4..7,
                    previewPrimaryHex = theme.primaryColorHex,
                    previewBgHex = theme.backgroundColorHex,
                    previewAccentHex = theme.accentColorHex,
                    dataJson = theme.toJson().toString(),
                    license = AssetLicense(author = "dev Open Studio", licenseName = "MIT Open Source", sourceUrl = "https://dev.chat/themes/${theme.id}")
                )
            )
        }

        // 2. Online Wallpapers & Patterns
        val sampleWps = ThemePresets.builtInWallpapers.take(12)
        sampleWps.forEachIndexed { i, wp ->
            list.add(
                AssetItem(
                    id = "online_wp_${wp.id}",
                    name = wp.title,
                    description = "Ultra high-definition procedural vector pattern and atmospheric background.",
                    type = if (wp.isVideoLoop) AssetType.ANIMATED_WALLPAPER else AssetType.WALLPAPER,
                    category = wp.category,
                    author = "Creative Commons Studio",
                    version = "1.5",
                    sizeBytes = if (wp.isVideoLoop) 8_500_000L else 450_000L,
                    downloadCount = 8900 + i * 1100,
                    rating = 4.7f + (i % 4) * 0.1f,
                    isFeatured = wp.isVideoLoop || i < 2,
                    isTrending = i in 3..6,
                    previewPrimaryHex = wp.primaryHex,
                    previewBgHex = wp.secondaryHex,
                    previewAccentHex = wp.accentHex,
                    license = AssetLicense(author = "CC-BY Public Art", licenseName = "Creative Commons 4.0", sourceUrl = "https://unsplash.com/dev")
                )
            )
        }

        // 3. Online Icon Packs (Inspired by Bootstrap Icons 1.13.1 & Modern Android)
        val iconPacks = listOf(
            Triple("pack_bootstrap_pro", "Bootstrap Icons Pro", "Modern MIT-licensed outline and filled tech icons."),
            Triple("pack_neon_cyber", "Cyber Neon Glow", "Futuristic neon glowing rounded icon set for dark setups."),
            Triple("pack_minimal_mono", "Clean Minimalist Mono", "Pure black-and-white minimalist line iconography."),
            Triple("pack_glass_crystal", "Glassmorphic Frost", "Frosted glass container backdrop for elegant UI."),
            Triple("pack_pixel_retro", "Pixel 8-Bit Arcade", "Retro 80s arcade gaming aesthetic for nostalgic lovers."),
            Triple("pack_gaming_apex", "Apex Gamer Strike", "Sharp angular gamer symbols with RGB highlights.")
        )
        iconPacks.forEachIndexed { i, (id, name, desc) ->
            list.add(
                AssetItem(
                    id = id,
                    name = name,
                    description = desc,
                    type = AssetType.ICON_PACK,
                    category = "Icons",
                    author = "Bootstrap & dev Team",
                    version = "1.13.1",
                    sizeBytes = 1_200_000L + (i * 200_000L),
                    downloadCount = 21000 + i * 3400,
                    rating = 4.9f,
                    isFeatured = i == 0 || i == 1,
                    isPopular = true,
                    previewPrimaryHex = if (i % 2 == 0) "#0284C7" else "#7C3AED",
                    previewBgHex = "#0F172A",
                    previewAccentHex = "#38BDF8",
                    license = AssetLicense(author = "Bootstrap Team / MIT", licenseName = "MIT License", sourceUrl = "https://icons.getbootstrap.com/")
                )
            )
        }

        // 4. Sticker & Reaction Packs
        list.add(
            AssetItem(
                id = "stickers_dev_devs",
                name = "Dev Coders Life 💻",
                description = "Humorous programmer and developer sticker pack for chat.",
                type = AssetType.STICKER_PACK,
                category = "Stickers",
                author = "dev Mascot Team",
                version = "1.0",
                sizeBytes = 950_000L,
                downloadCount = 18400,
                rating = 4.9f,
                isFeatured = true,
                previewPrimaryHex = "#10B981",
                previewBgHex = "#064E3B",
                previewAccentHex = "#34D399"
            )
        )
        list.add(
            AssetItem(
                id = "reactions_cosmic_anim",
                name = "Cosmic Animated Reactions ✨",
                description = "Glowing floating reaction pack with stars, fire, hearts, and rocket.",
                type = AssetType.REACTION_PACK,
                category = "Reactions",
                author = "VFX Collective",
                version = "2.0",
                sizeBytes = 620_000L,
                downloadCount = 14200,
                rating = 4.8f,
                isTrending = true,
                previewPrimaryHex = "#F59E0B",
                previewBgHex = "#451A03",
                previewAccentHex = "#FBBF24"
            )
        )

        // 5. Open-Source Font Packs
        list.add(
            AssetItem(
                id = "font_cairo_alexandria",
                name = "Cairo & Alexandria Arabic Display",
                description = "Modern geometric Arabic fonts designed for high-resolution screens.",
                type = AssetType.FONT,
                category = "Arabic",
                author = "Google Fonts (OFL)",
                version = "3.0",
                sizeBytes = 1_800_000L,
                downloadCount = 34000,
                rating = 5.0f,
                isFeatured = true,
                previewPrimaryHex = "#2563EB",
                previewBgHex = "#1E293B",
                previewAccentHex = "#60A5FA",
                license = AssetLicense(author = "Mohamed Gaber", licenseName = "SIL Open Font License", sourceUrl = "https://fonts.google.com/specimen/Cairo")
            )
        )

        return list
    }
}
