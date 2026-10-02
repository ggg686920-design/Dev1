package com.example.presentation.customization

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.assets.*
import com.example.core.customization.CustomizationManager
import com.example.core.customization.parseHexColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnlineAssetStoreScreen(
    assetManager: AssetManager,
    customizationManager: CustomizationManager,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val assets by assetManager.assets.collectAsState()
    val cacheInfo by assetManager.cacheInfo.collectAsState()
    val isWifiOnly by assetManager.isWifiOnly.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("الكل") }
    var selectedType by remember { mutableStateOf<AssetType?>(null) }
    var showCacheSheet by remember { mutableStateOf(false) }

    val filterOptions = listOf("الكل", "مميز (Featured)", "شائع (Trending)", "الأكثر شعبية", "جديد (New)", "موصى به", "مجاني (Free)", "AMOLED", "Cyber", "Glass", "Minimal", "Luxury", "Space", "Arabic")

    val filteredAssets = remember(assets, searchQuery, selectedFilter, selectedType) {
        assets.filter { item ->
            val matchesType = selectedType == null || item.type == selectedType
            val matchesSearch = searchQuery.isBlank() ||
                    item.name.contains(searchQuery, ignoreCase = true) ||
                    item.description.contains(searchQuery, ignoreCase = true) ||
                    item.category.contains(searchQuery, ignoreCase = true) ||
                    item.author.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "الكل" -> true
                "مميز (Featured)" -> item.isFeatured
                "شائع (Trending)" -> item.isTrending
                "الأكثر شعبية" -> item.isPopular
                "جديد (New)" -> true
                "موصى به" -> item.isRecommended
                "مجاني (Free)" -> item.isFree
                else -> item.category.contains(selectedFilter, ignoreCase = true)
            }

            matchesType && matchesSearch && matchesFilter
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("متجر الأصول السحابي (Asset Store)", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text("ثيمات، خلفيات، أيقونات، وخطوط قابلة للتنزيل", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showCacheSheet = true }) {
                        Icon(Icons.Default.Storage, contentDescription = "Storage Cache", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("ابحث عن Cyber, Minimal, Neon, AMOLED, Icons...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            )

            // Asset Type Filter Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedType == null,
                        onClick = { selectedType = null },
                        label = { Text("جميع الأنواع") }
                    )
                }
                items(AssetType.entries) { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        label = { Text(type.titleAr) }
                    )
                }
            }

            // Categories Filter Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filterOptions) { opt ->
                    FilterChip(
                        selected = selectedFilter == opt,
                        onClick = { selectedFilter = opt },
                        label = { Text(opt, fontSize = 11.sp) }
                    )
                }
            }

            // Assets List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
            ) {
                items(filteredAssets, key = { it.id }) { item ->
                    AssetCardItem(
                        item = item,
                        onDownload = {
                            assetManager.startDownload(item.id)
                            Toast.makeText(context, "بدأ تنزيل: ${item.name}", Toast.LENGTH_SHORT).show()
                        },
                        onPause = { assetManager.pauseDownload(item.id) },
                        onResume = { assetManager.resumeDownload(item.id) },
                        onCancel = { assetManager.cancelDownload(item.id) },
                        onDelete = {
                            assetManager.deleteDownloadedAsset(item.id)
                            Toast.makeText(context, "تم حذف الأصل من الجهاز", Toast.LENGTH_SHORT).show()
                        },
                        onApply = {
                            if (item.dataJson.isNotBlank()) {
                                customizationManager.importThemeFromJson(item.dataJson)
                            }
                            Toast.makeText(context, "تم تطبيق الأصل: ${item.name}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    // Storage & Cache Bottom Sheet
    if (showCacheSheet) {
        ModalBottomSheet(onDismissRequest = { showCacheSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("إدارة الذاكرة المؤقتة والتخزين (Cache)", fontWeight = FontWeight.Bold, fontSize = 17.sp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("التنزيل عبر Wi-Fi فقط للحفاظ على الباقة")
                    Switch(checked = isWifiOnly, onCheckedChange = { assetManager.setWifiOnly(it) })
                }

                HorizontalDivider()

                CacheRow("ذاكرة الثيمات المؤقتة", cacheInfo.themeCacheBytes) {
                    assetManager.clearCache(AssetType.THEME)
                    Toast.makeText(context, "تم مسح ذاكرة الثيمات", Toast.LENGTH_SHORT).show()
                }

                CacheRow("ذاكرة الخلفيات والصور", cacheInfo.wallpaperCacheBytes) {
                    assetManager.clearCache(AssetType.WALLPAPER)
                    Toast.makeText(context, "تم مسح ذاكرة الخلفيات", Toast.LENGTH_SHORT).show()
                }

                CacheRow("ذاكرة حزم الأيقونات", cacheInfo.iconCacheBytes) {
                    assetManager.clearCache(AssetType.ICON_PACK)
                    Toast.makeText(context, "تم مسح ذاكرة الأيقونات", Toast.LENGTH_SHORT).show()
                }

                CacheRow("ذاكرة الخطوط", cacheInfo.fontCacheBytes) {
                    assetManager.clearCache(AssetType.FONT)
                    Toast.makeText(context, "تم مسح ذاكرة الخطوط", Toast.LENGTH_SHORT).show()
                }

                CacheRow("ذاكرة الفيديو المتحرك", cacheInfo.videoWallpaperCacheBytes) {
                    assetManager.clearCache(AssetType.ANIMATED_WALLPAPER)
                    Toast.makeText(context, "تم مسح ذاكرة الفيديو", Toast.LENGTH_SHORT).show()
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        assetManager.clearCache(null)
                        Toast.makeText(context, "تم تفريغ كافة الذاكرة المؤقتة", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("مسح كامل الذاكرة المؤقتة (Clear All Cache)")
                }
            }
        }
    }
}

@Composable
private fun CacheRow(label: String, bytes: Long, onClear: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(label, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text("${bytes / (1024 * 1024)} ميجابايت", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        TextButton(onClick = onClear) {
            Text("مسح", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }
    }
}

@Composable
private fun AssetCardItem(
    item: AssetItem,
    onDownload: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit,
    onDelete: () -> Unit,
    onApply: () -> Unit
) {
    val primaryColor = parseHexColor(item.previewPrimaryHex)
    val bgColor = parseHexColor(item.previewBgHex)
    val accentColor = parseHexColor(item.previewAccentHex)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Asset thumbnail preview
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(bgColor)
                        .border(1.5.dp, primaryColor, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(accentColor)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (item.isFeatured) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("مميز ⭐", fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }
                    }

                    Text(
                        text = "${item.author} • ${item.category} • v${item.version}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("⭐ ${item.rating}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                        Text("⬇ ${item.downloadCount}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("💾 ${item.sizeBytes / 1024} KB", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Download Progress Bar if downloading
            if (item.downloadStatus == DownloadStatus.DOWNLOADING || item.downloadStatus == DownloadStatus.PAUSED) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { item.downloadProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (item.downloadStatus == DownloadStatus.PAUSED) "مؤقت (Paused)" else "جارٍ التنزيل والتحقق...",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("${(item.downloadProgress * 100).toInt()}%", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (item.downloadStatus) {
                    DownloadStatus.NOT_DOWNLOADED, DownloadStatus.FAILED -> {
                        Button(
                            onClick = onDownload,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تنزيل مجاني", fontSize = 12.sp)
                        }
                    }

                    DownloadStatus.DOWNLOADING -> {
                        OutlinedButton(
                            onClick = onPause,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Pause, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("إيقاف مؤقت", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = onCancel,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("إلغاء", fontSize = 11.sp)
                        }
                    }

                    DownloadStatus.PAUSED -> {
                        Button(
                            onClick = onResume,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("استئناف", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = onCancel,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("إلغاء", fontSize = 11.sp)
                        }
                    }

                    DownloadStatus.DOWNLOADED -> {
                        Button(
                            onClick = onApply,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تطبيق الأصل", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }

                    DownloadStatus.UPDATE_AVAILABLE -> {
                        Button(
                            onClick = onDownload,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("تحديث متوفر ⚡", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
