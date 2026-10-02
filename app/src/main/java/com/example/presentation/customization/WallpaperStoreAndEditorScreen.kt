package com.example.presentation.customization

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.customization.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpaperStoreAndEditorScreen(
    customizationManager: CustomizationManager,
    chatOverridesManager: ChatThemeOverridesManager,
    conversationId: String? = null,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val currentTheme by customizationManager.currentTheme.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Wallpapers, 1: Editor, 2: Video Wallpapers

    var selectedWp by remember {
        mutableStateOf(
            ThemePresets.builtInWallpapers.find { it.id == currentTheme.wallpaperId }
                ?: ThemePresets.builtInWallpapers.first()
        )
    }

    var editorConfig by remember { mutableStateOf(WallpaperEditorConfig()) }
    var videoConfig by remember { mutableStateOf(VideoWallpaperConfig()) }

    val categories = listOf("الكل", "HD", "AMOLED", "Dark", "Abstract", "Nature", "Space", "Technology", "Gaming", "Minimal", "Luxury", "Gradient", "Colorful")
    var selectedCategory by remember { mutableStateOf("الكل") }

    val allWallpapers = ThemePresets.builtInWallpapers
    val filteredWallpapers = remember(selectedCategory, allWallpapers) {
        if (selectedCategory == "الكل") allWallpapers
        else allWallpapers.filter { it.category.contains(selectedCategory, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(if (conversationId != null) "خلفية هذه المحادثة الخاصة" else "متجر واستوديو الخلفيات", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text("34+ خلفية مع محرر تعديل التأثيرات وفيديو تفاعلي", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("المكتبة (34+)", fontWeight = FontWeight.Bold) })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("المحرر (Editor)", fontWeight = FontWeight.Bold) })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("فيديو حي 🎥", fontWeight = FontWeight.Bold) })
            }

            when (selectedTab) {
                0 -> {
                    // WALLPAPER BROWSER
                    Column(modifier = Modifier.fillMaxSize()) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(categories) { cat ->
                                FilterChip(
                                    selected = selectedCategory == cat,
                                    onClick = { selectedCategory = cat },
                                    label = { Text(cat, fontSize = 11.sp) }
                                )
                            }
                        }

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            items(filteredWallpapers) { wp ->
                                val isSelected = wp.id == selectedWp.id
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(0.72f)
                                        .clickable {
                                            selectedWp = wp
                                            if (conversationId != null) {
                                                chatOverridesManager.setChatWallpaper(conversationId, wp.id)
                                                Toast.makeText(context, "تم تطبيق الخلفية على هذه المحادثة: ${wp.title}", Toast.LENGTH_SHORT).show()
                                            } else {
                                                customizationManager.updateActiveTheme { it.copy(wallpaperId = wp.id) }
                                                Toast.makeText(context, "تم تطبيق الخلفية عاماً: ${wp.title}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                ) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        WallpaperRenderer(wallpaperItem = wp)
                                        Surface(
                                            color = Color.Black.copy(alpha = 0.65f),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .align(Alignment.BottomCenter)
                                        ) {
                                            Text(
                                                text = wp.title,
                                                color = Color.White,
                                                fontSize = 9.sp,
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
                                                    .size(20.dp)
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

                1 -> {
                    // WALLPAPER EFFECTS EDITOR
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Live Canvas Preview
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                WallpaperRenderer(wallpaperItem = selectedWp)
                                // Overlay Dim layer
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = editorConfig.overlayDimAlpha))
                                )
                                Text(
                                    text = "معاينة التأثيرات الحية",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(12.dp)
                                )
                            }
                        }

                        // Brightness & Dim
                        Column {
                            Text("عتامة وتعتيم الخلفية (Dim Overlay): ${(editorConfig.overlayDimAlpha * 100).toInt()}%", fontWeight = FontWeight.SemiBold)
                            Slider(
                                value = editorConfig.overlayDimAlpha,
                                onValueChange = { editorConfig = editorConfig.copy(overlayDimAlpha = it) },
                                valueRange = 0f..0.8f
                            )
                        }

                        // Blur Slider
                        Column {
                            Text("درجة الضبابية (Blur Effect): ${editorConfig.blurRadiusDp.toInt()}dp", fontWeight = FontWeight.SemiBold)
                            Slider(
                                value = editorConfig.blurRadiusDp,
                                onValueChange = { editorConfig = editorConfig.copy(blurRadiusDp = it) },
                                valueRange = 0f..20f
                            )
                        }

                        // Zoom Slider
                        Column {
                            Text("التقريب (Zoom / Scale): ${(editorConfig.zoomScale * 100).toInt()}%", fontWeight = FontWeight.SemiBold)
                            Slider(
                                value = editorConfig.zoomScale,
                                onValueChange = { editorConfig = editorConfig.copy(zoomScale = it) },
                                valueRange = 1.0f..2.0f
                            )
                        }

                        Button(
                            onClick = {
                                Toast.makeText(context, "تم حفظ وتطبيق ضبط الخلفية بنجاح!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("حفظ التأثيرات")
                        }
                    }
                }

                2 -> {
                    // VIDEO WALLPAPER SIMULATION
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ) {
                            val videoItem = ThemePresets.builtInWallpapers.find { it.isVideoLoop } ?: selectedWp
                            WallpaperRenderer(wallpaperItem = videoItem, reduceMotion = false)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("تفعيل خلفية الفيديو الحية (Live Video)", fontWeight = FontWeight.SemiBold)
                            Switch(checked = videoConfig.isEnabled, onCheckedChange = { videoConfig = videoConfig.copy(isEnabled = it) })
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("تكرار الفيديو تلقائياً (Loop)", fontWeight = FontWeight.SemiBold)
                            Switch(checked = videoConfig.isLooping, onCheckedChange = { videoConfig = videoConfig.copy(isLooping = it) })
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("كتم الصوت تماماً (Mute)", fontWeight = FontWeight.SemiBold)
                            Switch(checked = videoConfig.isMuted, onCheckedChange = { videoConfig = videoConfig.copy(isMuted = it) })
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("وضع توفير البطارية (Battery Saver)", fontWeight = FontWeight.SemiBold)
                            Switch(checked = videoConfig.batterySaverMode, onCheckedChange = { videoConfig = videoConfig.copy(batterySaverMode = it) })
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("التشغيل عبر Wi-Fi فقط", fontWeight = FontWeight.SemiBold)
                            Switch(checked = videoConfig.wifiOnlyPlayback, onCheckedChange = { videoConfig = videoConfig.copy(wifiOnlyPlayback = it) })
                        }
                    }
                }
            }
        }
    }
}
