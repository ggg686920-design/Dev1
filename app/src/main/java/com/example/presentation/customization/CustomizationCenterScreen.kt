package com.example.presentation.customization

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.customization.*

enum class CustomizationTab(val titleAr: String, val titleEn: String) {
    THEMES("الثيمات", "Themes"),
    STORE("المتجر", "Theme Store"),
    WALLPAPERS("الخلفيات", "Wallpapers"),
    BUBBLES("الفقاعات", "Bubbles"),
    ICONS("الأيقونات", "Icons"),
    TYPOGRAPHY("الخطوط", "Typography"),
    UI_STYLE("عناصر الواجهة", "UI & Buttons"),
    EFFECTS("المؤثرات", "Effects & Anim"),
    MY_ASSETS("حزمي وتصدير", "My Assets")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizationCenterScreen(
    customizationManager: CustomizationManager,
    onNavigateBack: () -> Unit,
    onNavigateToOnlineStore: () -> Unit = {},
    onNavigateToHomeDesigner: () -> Unit = {},
    onNavigateToChatDesigner: () -> Unit = {},
    onNavigateToColorTypographyStudio: () -> Unit = {},
    onNavigateToIconStore: () -> Unit = {},
    onNavigateToWallpaperStore: () -> Unit = {}
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val currentTheme by customizationManager.currentTheme.collectAsState()
    val customThemes by customizationManager.customThemes.collectAsState()
    val favorites by customizationManager.favoriteThemeIds.collectAsState()

    var selectedTab by remember { mutableStateOf(CustomizationTab.THEMES) }
    var previewTheme by remember { mutableStateOf<ThemeConfig?>(null) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showSaveThemeDialog by remember { mutableStateOf(false) }

    // Live preview dialog trigger
    previewTheme?.let { theme ->
        LiveThemePreviewDialog(
            theme = theme,
            onDismiss = { previewTheme = null },
            onApply = {
                customizationManager.applyTheme(theme)
                previewTheme = null
                Toast.makeText(context, "تم تطبيق الثيم: ${theme.name}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("مركز تخصيص dev", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("تحكم في 90% من مظهر واجهة التطبيق", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToOnlineStore
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = "Online Store", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(
                        onClick = {
                            val surprise = customizationManager.generateRandomSurpriseTheme()
                            customizationManager.applyTheme(surprise)
                            Toast.makeText(context, "🎲 تم توليد وتطبيق ثيم عشوائي متناسق!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.Casino, contentDescription = "Surprise Me", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(
                        onClick = {
                            customizationManager.resetToDefaults()
                            Toast.makeText(context, "تمت إعادة تعيين الثيم للافتراضي", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.RestartAlt, contentDescription = "Reset", tint = MaterialTheme.colorScheme.onSurfaceVariant)
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
            // Dedicated Studios Quick Action Banner
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    AssistChip(
                        onClick = onNavigateToOnlineStore,
                        label = { Text("🛍️ متجر الأصول السحابي", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
                item {
                    AssistChip(
                        onClick = onNavigateToColorTypographyStudio,
                        label = { Text("🎨 استوديو الألوان والخطوط", fontSize = 12.sp) }
                    )
                }
                item {
                    AssistChip(
                        onClick = onNavigateToChatDesigner,
                        label = { Text("💬 مصمم الشات المتقدم", fontSize = 12.sp) }
                    )
                }
                item {
                    AssistChip(
                        onClick = onNavigateToHomeDesigner,
                        label = { Text("📱 مصمم شاشة الهوم", fontSize = 12.sp) }
                    )
                }
                item {
                    AssistChip(
                        onClick = onNavigateToIconStore,
                        label = { Text("🖼️ متجر الأيقونات", fontSize = 12.sp) }
                    )
                }
                item {
                    AssistChip(
                        onClick = onNavigateToWallpaperStore,
                        label = { Text("🌌 محرر الخلفيات", fontSize = 12.sp) }
                    )
                }
            }
            // Scrollable Category Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab.ordinal,
                edgePadding = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                CustomizationTab.entries.forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = {
                            Text(
                                text = tab.titleAr,
                                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }

            // Tab Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (selectedTab) {
                    CustomizationTab.THEMES -> ThemesTabContent(
                        currentTheme = currentTheme,
                        favorites = favorites,
                        onPreview = { previewTheme = it },
                        onApply = {
                            customizationManager.applyTheme(it)
                            Toast.makeText(context, "تم تطبيق: ${it.name}", Toast.LENGTH_SHORT).show()
                        },
                        onToggleFavorite = { customizationManager.toggleFavorite(it) }
                    )
                    CustomizationTab.STORE -> StoreTabContent(
                        currentTheme = currentTheme,
                        onPreview = { previewTheme = it },
                        onApply = {
                            customizationManager.applyTheme(it)
                            Toast.makeText(context, "تم تثبيت وتطبيق الثيم من المتجر!", Toast.LENGTH_SHORT).show()
                        },
                        onOpenFullStore = onNavigateToOnlineStore
                    )
                    CustomizationTab.WALLPAPERS -> WallpapersTabContent(
                        currentTheme = currentTheme,
                        onSelectWallpaper = { wp ->
                            customizationManager.updateActiveTheme { it.copy(wallpaperId = wp.id) }
                            Toast.makeText(context, "تم تعيين الخلفية: ${wp.title}", Toast.LENGTH_SHORT).show()
                        }
                    )
                    CustomizationTab.BUBBLES -> BubblesTabContent(
                        currentTheme = currentTheme,
                        onUpdateBubble = { newBubble ->
                            customizationManager.updateActiveTheme { it.copy(bubbleConfig = newBubble) }
                        }
                    )
                    CustomizationTab.ICONS -> IconsTabContent(
                        currentTheme = currentTheme,
                        onSelectIconStyle = { style ->
                            customizationManager.updateActiveTheme { it.copy(iconStyle = style) }
                            Toast.makeText(context, "تم تغيير نمط الأيقونات إلى: ${style.titleAr}", Toast.LENGTH_SHORT).show()
                        }
                    )
                    CustomizationTab.TYPOGRAPHY -> TypographyTabContent(
                        currentTheme = currentTheme,
                        onUpdateTypography = { newTypo ->
                            customizationManager.updateActiveTheme { it.copy(typography = newTypo) }
                        }
                    )
                    CustomizationTab.UI_STYLE -> UiStyleTabContent(
                        currentTheme = currentTheme,
                        onUpdateTheme = { newTheme ->
                            customizationManager.applyTheme(newTheme)
                        }
                    )
                    CustomizationTab.EFFECTS -> EffectsTabContent(
                        currentTheme = currentTheme,
                        onUpdateTheme = { newTheme ->
                            customizationManager.applyTheme(newTheme)
                        }
                    )
                    CustomizationTab.MY_ASSETS -> MyAssetsTabContent(
                        currentTheme = currentTheme,
                        customThemes = customThemes,
                        favorites = favorites,
                        onApply = { customizationManager.applyTheme(it) },
                        onDeleteCustom = { customizationManager.deleteCustomTheme(it) },
                        onSaveCurrentAsCustom = { showSaveThemeDialog = true },
                        onExportCurrent = {
                            val json = customizationManager.exportThemeToJson(currentTheme)
                            clipboardManager.setText(AnnotatedString(json))
                            Toast.makeText(context, "تم نسخ كود الثيم (.devtheme) إلى الحافظة بنجاح!", Toast.LENGTH_LONG).show()
                        },
                        onImport = { showImportDialog = true }
                    )
                }
            }
        }
    }

    // Save Theme Dialog
    if (showSaveThemeDialog) {
        var themeName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showSaveThemeDialog = false },
            title = { Text("حفظ الثيم الحالي") },
            text = {
                OutlinedTextField(
                    value = themeName,
                    onValueChange = { themeName = it },
                    label = { Text("اسم الثيم المخصص") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (themeName.isNotBlank()) {
                            customizationManager.saveAsCustomTheme(themeName, currentTheme)
                            showSaveThemeDialog = false
                            Toast.makeText(context, "تم حفظ الثيم: $themeName", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("حفظ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveThemeDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Import .devtheme Dialog
    if (showImportDialog) {
        var importJson by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("استيراد ثيم (.devtheme)") },
            text = {
                Column {
                    Text("الصق نص كود الثيم بصيغة JSON الآمنة:", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = importJson,
                        onValueChange = { importJson = it },
                        placeholder = { Text("{\"format\":\"devtheme\", ...}") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val result = customizationManager.importThemeFromJson(importJson)
                        if (result.isSuccess) {
                            val imported = result.getOrNull()!!
                            customizationManager.applyTheme(imported)
                            showImportDialog = false
                            Toast.makeText(context, "تم استيراد وتطبيق الثيم: ${imported.name}", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "خطأ: الملف غير صالح أو تالف", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("استيراد وتطبيق")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

// ----------------------------------------------------------------------
// 1. THEMES TAB (32+ Built-in + Category Filter)
// ----------------------------------------------------------------------
@Composable
private fun ThemesTabContent(
    currentTheme: ThemeConfig,
    favorites: Set<String>,
    onPreview: (ThemeConfig) -> Unit,
    onApply: (ThemeConfig) -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    val categories = listOf("الكل", "Modern", "AMOLED", "Cyberpunk", "Gaming", "Glass", "Light", "Luxury", "Nature", "Space", "Ocean", "Sunset", "Purple", "Pink", "Retro", "Gradient")
    var selectedCategory by remember { mutableStateOf("الكل") }

    val filteredThemes = remember(selectedCategory) {
        if (selectedCategory == "الكل") ThemePresets.builtInThemes
        else ThemePresets.builtInThemes.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Category Pills
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat, fontSize = 12.sp) }
                )
            }
        }

        // Themes Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(filteredThemes) { theme ->
                val isCurrent = theme.id == currentTheme.id
                val isFav = favorites.contains(theme.id)
                val primary = parseHexColor(theme.primaryColorHex)
                val bg = parseHexColor(theme.backgroundColorHex)
                val surface = parseHexColor(theme.surfaceColorHex)

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = surface),
                    border = if (isCurrent) BorderStroke(2.dp, primary) else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPreview(theme) }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Visual Palette Preview
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(bg)
                                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                .padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Mini incoming bubble
                                Box(
                                    modifier = Modifier
                                        .size(36.dp, 24.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(parseHexColor(theme.bubbleConfig.incomingBgHex))
                                )
                                // Mini outgoing bubble
                                Box(
                                    modifier = Modifier
                                        .size(36.dp, 24.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(parseHexColor(theme.bubbleConfig.outgoingBgHex))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Theme title + Favorite star
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = theme.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { onToggleFavorite(theme.id) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (isFav) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                    contentDescription = null,
                                    tint = if (isFav) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Text(
                            text = "${theme.category} • ${if (theme.isDark) "داكن" else "فاتح"}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onPreview(theme) },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("معاينة", fontSize = 11.sp)
                            }
                            Button(
                                onClick = { onApply(theme) },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isCurrent) MaterialTheme.colorScheme.secondary else primary
                                )
                            ) {
                                Text(if (isCurrent) "مفعّل" else "تطبيق", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------
// 2. STORE TAB (Online Library Ready for Future CDN / Cloud)
// ----------------------------------------------------------------------
@Composable
private fun StoreTabContent(
    currentTheme: ThemeConfig,
    onPreview: (ThemeConfig) -> Unit,
    onApply: (ThemeConfig) -> Unit,
    onOpenFullStore: () -> Unit = {}
) {
    val storeItems = ThemePresets.storeItems
    var searchQuery by remember { mutableStateOf("") }

    val filteredItems = remember(searchQuery) {
        if (searchQuery.isBlank()) storeItems
        else storeItems.filter { it.title.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner to Open Full Online Asset Library
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenFullStore() }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDownload,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "المتجر السحابي المتكامل للأصول (Online Hub)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = "حمّل ثيمات، خلفيات، حزم أيقونات ومؤثرات أونلاين مع إدارة التنزيلات والتخزين المؤقت",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.85f)
                        )
                    }
                    Button(
                        onClick = onOpenFullStore,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("فتح", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        // Search bar in store
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("ابحث في متجر الثيمات...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )
        }

        // Featured Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("مميز اليوم ⭐", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("مجموعة ثيمات Cyber Matrix Pro", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("حزم متكاملة مجانية وعالية الدقة مهيأة للاستخدام دون إنترنت.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = { onApply(ThemePresets.builtInThemes[3]) },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("تحميل")
                    }
                }
            }
        }

        // Section: Trending
        item {
            Text("الأكثر تحميلاً ورواجاً 🔥", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        items(filteredItems) { item ->
            val pColor = parseHexColor(item.previewPrimaryHex)
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPreview(item.themeConfig) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(parseHexColor(item.previewBgHex))
                            .border(2.dp, pColor, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(pColor)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("${item.author} • ${item.category}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${item.rating}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("⬇ ${item.downloadsCount}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onApply(item.themeConfig) },
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("تطبيق", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------
// 3. WALLPAPERS TAB (32+ Built-in + Video Wallpaper simulation)
// ----------------------------------------------------------------------
@Composable
private fun WallpapersTabContent(
    currentTheme: ThemeConfig,
    onSelectWallpaper: (WallpaperItem) -> Unit
) {
    val wallpapers = ThemePresets.builtInWallpapers
    val categories = listOf("الكل", "Technology", "AMOLED", "Dark", "Space", "Gradient", "Abstract", "Gaming", "Nature", "Luxury", "Minimal", "Colorful")
    var selectedCategory by remember { mutableStateOf("الكل") }

    val filtered = remember(selectedCategory) {
        if (selectedCategory == "الكل") wallpapers
        else wallpapers.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat, fontSize = 12.sp) }
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(filtered) { wp ->
                val isSelected = wp.id == currentTheme.wallpaperId

                Card(
                    shape = RoundedCornerShape(16.dp),
                    border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.7f)
                        .clickable { onSelectWallpaper(wp) }
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        WallpaperRenderer(wallpaperItem = wp)

                        if (wp.isVideoLoop) {
                            Surface(
                                color = Color.Black.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(6.dp)
                            ) {
                                Text("LIVE 🎥", color = Color.White, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }

                        // Bottom label
                        Surface(
                            color = Color.Black.copy(alpha = 0.7f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                        ) {
                            Text(
                                text = wp.title,
                                color = Color.White,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(4.dp)
                            )
                        }

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(6.dp)
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------
// 4. BUBBLE DESIGNER TAB
// ----------------------------------------------------------------------
@Composable
private fun BubblesTabContent(
    currentTheme: ThemeConfig,
    onUpdateBubble: (BubbleConfig) -> Unit
) {
    val bubble = currentTheme.bubbleConfig
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Live Bubble Preview Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = parseHexColor(currentTheme.surfaceColorHex)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(bubble.spacingDp.dp)
            ) {
                Text("المعاينة الفورية لفقاعات المحادثة", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                // Incoming preview
                Box(
                    modifier = Modifier
                        .widthIn(max = 260.dp)
                        .then(if (bubble.hasShadow) Modifier.shadow(3.dp, RoundedCornerShape(bubble.radiusDp.dp)) else Modifier)
                        .clip(RoundedCornerShape(bubble.radiusDp.dp))
                        .background(parseHexColor(bubble.incomingBgHex).copy(alpha = bubble.transparency))
                        .then(if (bubble.hasBorder) Modifier.border(bubble.borderWidthDp.dp, parseHexColor(bubble.borderColorHex), RoundedCornerShape(bubble.radiusDp.dp)) else Modifier)
                        .padding(bubble.paddingDp.dp)
                ) {
                    Text("هذا نص رسالة واردة في المحادثة", color = parseHexColor(bubble.incomingTextHex), fontSize = 14.sp)
                }

                // Outgoing preview
                Box(
                    modifier = Modifier
                        .align(Alignment.End)
                        .widthIn(max = 260.dp)
                        .then(if (bubble.hasShadow) Modifier.shadow(3.dp, RoundedCornerShape(bubble.radiusDp.dp)) else Modifier)
                        .clip(RoundedCornerShape(bubble.radiusDp.dp))
                        .background(parseHexColor(bubble.outgoingBgHex).copy(alpha = bubble.transparency))
                        .then(if (bubble.hasBorder) Modifier.border(bubble.borderWidthDp.dp, parseHexColor(bubble.borderColorHex), RoundedCornerShape(bubble.radiusDp.dp)) else Modifier)
                        .padding(bubble.paddingDp.dp)
                ) {
                    Text("وهذا نص رسالتي الصادرة في المحادثة ✓✓", color = parseHexColor(bubble.outgoingTextHex), fontSize = 14.sp)
                }
            }
        }

        // Shape Selection
        Text("شكل الفقاعة (Bubble Shape)", fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            BubbleShape.entries.forEach { shape ->
                FilterChip(
                    selected = bubble.shape == shape,
                    onClick = { onUpdateBubble(bubble.copy(shape = shape)) },
                    label = { Text(shape.titleAr, fontSize = 11.sp) }
                )
            }
        }

        // Radius Slider
        Column {
            Text("انحناء الحواف (Radius): ${bubble.radiusDp}dp", fontWeight = FontWeight.SemiBold)
            Slider(
                value = bubble.radiusDp.toFloat(),
                onValueChange = { onUpdateBubble(bubble.copy(radiusDp = it.toInt())) },
                valueRange = 0f..32f,
                steps = 31
            )
        }

        // Padding Slider
        Column {
            Text("المسافة الداخلية (Padding): ${bubble.paddingDp}dp", fontWeight = FontWeight.SemiBold)
            Slider(
                value = bubble.paddingDp.toFloat(),
                onValueChange = { onUpdateBubble(bubble.copy(paddingDp = it.toInt())) },
                valueRange = 6f..24f,
                steps = 17
            )
        }

        // Spacing Slider
        Column {
            Text("المسافة بين الرسائل (Spacing): ${bubble.spacingDp}dp", fontWeight = FontWeight.SemiBold)
            Slider(
                value = bubble.spacingDp.toFloat(),
                onValueChange = { onUpdateBubble(bubble.copy(spacingDp = it.toInt())) },
                valueRange = 4f..20f,
                steps = 15
            )
        }

        // Toggles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("ظلال الفقاعات (Drop Shadow)", fontWeight = FontWeight.SemiBold)
            Switch(checked = bubble.hasShadow, onCheckedChange = { onUpdateBubble(bubble.copy(hasShadow = it)) })
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("حد خارجي مضيء (Border)", fontWeight = FontWeight.SemiBold)
            Switch(
                checked = bubble.hasBorder,
                onCheckedChange = { onUpdateBubble(bubble.copy(hasBorder = it, borderWidthDp = if (it) 1 else 0)) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("ذيل الفقاعة (Chat Tail)", fontWeight = FontWeight.SemiBold)
            Switch(checked = bubble.hasTail, onCheckedChange = { onUpdateBubble(bubble.copy(hasTail = it)) })
        }
    }
}

// ----------------------------------------------------------------------
// 5. ICONS TAB (10+ Icon Styles)
// ----------------------------------------------------------------------
@Composable
private fun IconsTabContent(
    currentTheme: ThemeConfig,
    onSelectIconStyle: (IconStyle) -> Unit
) {
    val styles = IconStyle.entries
    val actionsToPreview = listOf(
        AppIconAction.HOME,
        AppIconAction.CHATS,
        AppIconAction.SEND,
        AppIconAction.VOICE,
        AppIconAction.CAMERA,
        AppIconAction.GALLERY,
        AppIconAction.SETTINGS
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("اختر نمط الأيقونات عبر التطبيق (10+ Styles)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("يؤثر هذا الاختيار فوراً على جميع شاشات المحادثات والقوائم والأزرار.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        items(styles) { style ->
            val isSelected = style == currentTheme.iconStyle

            Card(
                shape = RoundedCornerShape(16.dp),
                border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectIconStyle(style) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(style.titleAr, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(style.titleEn, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(modifier = Modifier.height(10.dp))

                        // Preview icons row
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            actionsToPreview.forEach { act ->
                                ThemedIcon(
                                    action = act,
                                    style = style,
                                    tint = MaterialTheme.colorScheme.primary,
                                    size = 22.dp
                                )
                            }
                        }
                    }

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------
// 6. TYPOGRAPHY TAB
// ----------------------------------------------------------------------
@Composable
private fun TypographyTabContent(
    currentTheme: ThemeConfig,
    onUpdateTypography: (TypographyConfig) -> Unit
) {
    val typo = currentTheme.typography
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Live Sample Text Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("معاينة الخط العربي والإنجليزي", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "تطبيق dev: مراسلة فورية وتخصيص لانهائي مع الحفاظ الكامل على RTL.",
                    fontSize = (15 * typo.fontSizeScale).sp,
                    fontWeight = if (typo.fontWeightBold) FontWeight.Bold else FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "The quick brown fox jumps over the lazy developer. 0123456789",
                    fontSize = (13 * typo.fontSizeScale).sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Font Family Selector
        Text("عائلة الخط (Font Family)", fontWeight = FontWeight.Bold)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FontFamilyType.entries.forEach { font ->
                FilterChip(
                    selected = typo.fontFamilyType == font,
                    onClick = { onUpdateTypography(typo.copy(fontFamilyType = font)) },
                    label = { Text(font.titleAr) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Font Size Scale Slider
        Column {
            Text("حجم الخط: ${(typo.fontSizeScale * 100).toInt()}%", fontWeight = FontWeight.SemiBold)
            Slider(
                value = typo.fontSizeScale,
                onValueChange = { onUpdateTypography(typo.copy(fontSizeScale = it)) },
                valueRange = 0.8f..1.35f,
                steps = 10
            )
        }

        // Formatting Toggles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("خط عريض افتراضياً (Bold)", fontWeight = FontWeight.SemiBold)
            Switch(checked = typo.fontWeightBold, onCheckedChange = { onUpdateTypography(typo.copy(fontWeightBold = it)) })
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("توهج النصوص (Text Glow)", fontWeight = FontWeight.SemiBold)
            Switch(checked = typo.hasGlow, onCheckedChange = { onUpdateTypography(typo.copy(hasGlow = it)) })
        }
    }
}

// ----------------------------------------------------------------------
// 7. UI STYLE & BUTTONS TAB
// ----------------------------------------------------------------------
@Composable
private fun UiStyleTabContent(
    currentTheme: ThemeConfig,
    onUpdateTheme: (ThemeConfig) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("شريط التنقل السفلي (Bottom Navigation)", fontWeight = FontWeight.Bold)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            NavBarStyle.entries.forEach { style ->
                FilterChip(
                    selected = currentTheme.navBarStyle == style,
                    onClick = { onUpdateTheme(currentTheme.copy(navBarStyle = style)) },
                    label = { Text(style.titleAr) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        HorizontalDivider()

        Text("شريط إدخال الرسائل (Input Bar)", fontWeight = FontWeight.Bold)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            InputBarStyle.entries.forEach { style ->
                FilterChip(
                    selected = currentTheme.inputBarStyle == style,
                    onClick = { onUpdateTheme(currentTheme.copy(inputBarStyle = style)) },
                    label = { Text(style.titleAr) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        HorizontalDivider()

        Text("نمط الأزرار (Button Style)", fontWeight = FontWeight.Bold)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            ButtonStyle.entries.forEach { style ->
                FilterChip(
                    selected = currentTheme.buttonStyle == style,
                    onClick = { onUpdateTheme(currentTheme.copy(buttonStyle = style)) },
                    label = { Text(style.titleAr) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        HorizontalDivider()

        Column {
            Text("انحناء بطاقات المحادثات (Card Radius): ${currentTheme.cardRadiusDp}dp", fontWeight = FontWeight.SemiBold)
            Slider(
                value = currentTheme.cardRadiusDp.toFloat(),
                onValueChange = { onUpdateTheme(currentTheme.copy(cardRadiusDp = it.toInt())) },
                valueRange = 0f..28f,
                steps = 27
            )
        }
    }
}

// ----------------------------------------------------------------------
// 8. EFFECTS & ANIMATION TAB
// ----------------------------------------------------------------------
@Composable
private fun EffectsTabContent(
    currentTheme: ThemeConfig,
    onUpdateTheme: (ThemeConfig) -> Unit
) {
    val effects = currentTheme.effects
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("سرعة ونمط الحركة (Animation Engine)", fontWeight = FontWeight.Bold)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            AnimationSpeed.entries.forEach { speed ->
                FilterChip(
                    selected = currentTheme.animationSpeed == speed,
                    onClick = { onUpdateTheme(currentTheme.copy(animationSpeed = speed)) },
                    label = { Text(speed.titleAr) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("تقليل الحركة لتوفير البطارية (Reduce Motion)", fontWeight = FontWeight.SemiBold)
            Switch(
                checked = effects.reduceMotion,
                onCheckedChange = { onUpdateTheme(currentTheme.copy(effects = effects.copy(reduceMotion = it))) }
            )
        }

        HorizontalDivider()

        Text("المؤثرات البصرية (Visual Effects)", fontWeight = FontWeight.Bold)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("تأثير الزجاج الشفاف (Glassmorphism)", fontWeight = FontWeight.SemiBold)
            Switch(
                checked = effects.glassmorphism,
                onCheckedChange = { onUpdateTheme(currentTheme.copy(effects = effects.copy(glassmorphism = it))) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("ضبابية الخلفية (Ambient Blur)", fontWeight = FontWeight.SemiBold)
            Switch(
                checked = effects.blurEnabled,
                onCheckedChange = { onUpdateTheme(currentTheme.copy(effects = effects.copy(blurEnabled = it))) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("توهج النيون (Glow Accents)", fontWeight = FontWeight.SemiBold)
            Switch(
                checked = effects.glowEnabled,
                onCheckedChange = { onUpdateTheme(currentTheme.copy(effects = effects.copy(glowEnabled = it))) }
            )
        }

        HorizontalDivider()

        Text("الزخارف التزيينية (Decorations)", fontWeight = FontWeight.Bold)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            DecorationStyle.entries.forEach { decor ->
                FilterChip(
                    selected = currentTheme.decorationStyle == decor,
                    onClick = { onUpdateTheme(currentTheme.copy(decorationStyle = decor)) },
                    label = { Text(decor.titleAr) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// ----------------------------------------------------------------------
// 9. MY ASSETS & IMPORT/EXPORT TAB
// ----------------------------------------------------------------------
@Composable
private fun MyAssetsTabContent(
    currentTheme: ThemeConfig,
    customThemes: List<ThemeConfig>,
    favorites: Set<String>,
    onApply: (ThemeConfig) -> Unit,
    onDeleteCustom: (String) -> Unit,
    onSaveCurrentAsCustom: () -> Unit,
    onExportCurrent: () -> Unit,
    onImport: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quick Action Row: Save, Export, Import
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSaveCurrentAsCustom,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("حفظ الثيم", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onExportCurrent,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تصدير", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onImport,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.FileOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("استيراد", fontSize = 12.sp)
                }
            }
        }

        // Section: Custom Themes
        item {
            Text("الثيمات المخصصة المحفوظة (${customThemes.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        if (customThemes.isEmpty()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "لم تقم بحفظ ثيمات مخصصة بعد. اضغط على \"حفظ الثيم\" لحفظ التعديلات الحالية كحزمة خاصة.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(customThemes) { custom ->
                val pColor = parseHexColor(custom.primaryColorHex)
                Card(
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(pColor)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(custom.name, fontWeight = FontWeight.Bold)
                            Text("ثيم مخصص", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { onApply(custom) }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Apply", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = { onDeleteCustom(custom.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        // Section: Favorite Themes
        item {
            Text("الثيمات المفضلة ⭐ (${favorites.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        val favThemes = ThemePresets.builtInThemes.filter { favorites.contains(it.id) }
        if (favThemes.isEmpty()) {
            item {
                Text("لا توجد ثيمات مضافة للمفضلة حالياً. انقر على رمز النجمة بجانب أي ثيم لإضافته.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(favThemes) { fav ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(fav.name, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Button(
                            onClick = { onApply(fav) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("تطبيق", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
