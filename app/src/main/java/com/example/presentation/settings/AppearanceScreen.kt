package com.example.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.settings.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceScreen(
    settingsManager: AppSettingsManager,
    onNavigateBack: () -> Unit
) {
    val settings by settingsManager.settings.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("المظهر والتخصيص", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Live Preview Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(android.graphics.Color.parseColor(settings.wallpaperPreset.backgroundHex))
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "معاينة حية للمحادثة",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )

                    // Incoming message preview
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(
                            topStart = settings.bubbleRadius.radiusDp.dp,
                            topEnd = settings.bubbleRadius.radiusDp.dp,
                            bottomEnd = settings.bubbleRadius.radiusDp.dp,
                            bottomStart = 4.dp
                        ),
                        modifier = Modifier.widthIn(max = 240.dp)
                    ) {
                        Text(
                            text = "مرحباً! كيف يبدو شكل المحادثة لديك الآن؟ ✨",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }

                    // Outgoing message preview
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(
                            topStart = settings.bubbleRadius.radiusDp.dp,
                            topEnd = settings.bubbleRadius.radiusDp.dp,
                            bottomStart = settings.bubbleRadius.radiusDp.dp,
                            bottomEnd = 4.dp
                        ),
                        modifier = Modifier
                            .align(Alignment.End)
                            .widthIn(max = 240.dp)
                    ) {
                        Text(
                            text = "يبدو رائعاً جداً ومريحاً للعين! 🚀",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // 1. Theme Mode
            Text("نمط السمة (Theme)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = settings.themeMode == ThemeMode.SYSTEM,
                    onClick = { settingsManager.updateThemeMode(ThemeMode.SYSTEM) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
                ) {
                    Text("النظام")
                }
                SegmentedButton(
                    selected = settings.themeMode == ThemeMode.LIGHT,
                    onClick = { settingsManager.updateThemeMode(ThemeMode.LIGHT) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
                ) {
                    Text("فاتح")
                }
                SegmentedButton(
                    selected = settings.themeMode == ThemeMode.DARK,
                    onClick = { settingsManager.updateThemeMode(ThemeMode.DARK) },
                    shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
                ) {
                    Text("داكن")
                }
            }

            // 2. Accent Colors
            Text("لون التطبيق الأساسي (Accent Color)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(AccentColor.entries) { accent ->
                    val isSelected = settings.accentColor == accent
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { settingsManager.updateAccentColor(accent) }
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(accent.lightColor)
                                .then(
                                    if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                    else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = accent.displayNameAr.substringBefore("/").trim(),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // 3. Chat Wallpaper Presets
            Text("خلفية المحادثة (Wallpaper)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(WallpaperPreset.entries) { preset ->
                    val isSelected = settings.wallpaperPreset == preset
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(android.graphics.Color.parseColor(preset.backgroundHex))
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .size(width = 80.dp, height = 70.dp)
                            .clickable { settingsManager.updateWallpaperPreset(preset) }
                            .then(
                                if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                                else Modifier
                            )
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = preset.titleAr,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (preset.isDark) Color.White else Color.Black,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // 4. Bubble Corner Radius
            Text("شكل فقاعات الرسائل", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                BubbleRadius.entries.forEachIndexed { index, radius ->
                    SegmentedButton(
                        selected = settings.bubbleRadius == radius,
                        onClick = { settingsManager.updateBubbleRadius(radius) },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = BubbleRadius.entries.size)
                    ) {
                        Text(radius.titleAr.substringBefore("(").trim())
                    }
                }
            }

            // 5. Font Size Scale
            Text("حجم الخط في التطبيق", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                FontSizeScale.entries.forEachIndexed { index, scale ->
                    SegmentedButton(
                        selected = settings.fontSizeScale == scale,
                        onClick = { settingsManager.updateFontSizeScale(scale) },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = FontSizeScale.entries.size)
                    ) {
                        Text(scale.titleAr.substringBefore("(").trim())
                    }
                }
            }
        }
    }
}
