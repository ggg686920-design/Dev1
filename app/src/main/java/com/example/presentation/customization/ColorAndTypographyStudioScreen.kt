package com.example.presentation.customization

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.customization.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorAndTypographyStudioScreen(
    customizationManager: CustomizationManager,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val currentTheme by customizationManager.currentTheme.collectAsState()

    var primaryHex by remember { mutableStateOf(currentTheme.primaryColorHex) }
    var accentHex by remember { mutableStateOf(currentTheme.accentColorHex) }
    var bgHex by remember { mutableStateOf(currentTheme.backgroundColorHex) }
    var typoConfig by remember { mutableStateOf(currentTheme.typography) }

    val primaryColor = parseHexColor(primaryHex)
    val bgColor = parseHexColor(bgHex)
    val harmonies = remember(primaryColor) { ColorHarmonyEngine.generateHarmonies(primaryColor) }
    val contrastRatio = remember(primaryColor, bgColor) { ColorHarmonyEngine.calculateContrastRatio(primaryColor, bgColor) }

    val presetColors = listOf(
        "#0284C7", "#38BDF8", "#7C3AED", "#A855F7", "#E11D48", "#FB7185",
        "#059669", "#34D399", "#D97706", "#FBBF24", "#EA580C", "#0891B2",
        "#0F172A", "#1E293B", "#000000", "#FFFFFF"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("استوديو الألوان والخطوط (Color & Type)", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text("تناغم الألوان، فاحص التباين، والطباعة المتقدمة", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            customizationManager.updateActiveTheme {
                                it.copy(
                                    primaryColorHex = primaryHex,
                                    accentColorHex = accentHex,
                                    backgroundColorHex = bgHex,
                                    typography = typoConfig
                                )
                            }
                            Toast.makeText(context, "تم حفظ وتطبيق لوحة الألوان والخطوط بنجاح!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { innerPadding ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Contrast & Harmony Banner
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = bgColor),
                border = androidx.compose.foundation.BorderStroke(1.dp, primaryColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "معاينة التباين الحي (Contrast Ratio)",
                        color = primaryColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "نسبة التباين: ${"%.2f".format(contrastRatio)}:1 ${if (contrastRatio >= 4.5f) "✓ ممتاز ومتوافق مع معايير WCAG" else "⚠️ تباين منخفض"}",
                        color = parseHexColor(currentTheme.textColorHex),
                        fontSize = 12.sp
                    )
                }
            }

            // Primary Color Picker Palette
            Text("اللون الأساسي (Primary Color): $primaryHex", fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(presetColors) { hex ->
                    val c = parseHexColor(hex)
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(c)
                            .border(if (primaryHex == hex) 3.dp else 1.dp, Color.White, CircleShape)
                            .clickable { primaryHex = hex }
                    )
                }
            }

            // Harmony Palette Generation
            Text("توليد التناغم اللوني التلقائي (Color Harmony)", fontWeight = FontWeight.Bold)
            harmonies.forEach { (name, colors) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(name, fontSize = 12.sp, modifier = Modifier.width(110.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        colors.forEach { c ->
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(c)
                            )
                        }
                    }
                }
            }

            HorizontalDivider()

            // Typography Section
            Text("عائلة الخط (Font Family)", fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(FontFamilyType.entries) { font ->
                    FilterChip(
                        selected = typoConfig.fontFamilyType == font,
                        onClick = { typoConfig = typoConfig.copy(fontFamilyType = font) },
                        label = { Text(font.titleAr, fontSize = 11.sp) }
                    )
                }
            }

            // Font Scale Slider
            Column {
                Text("حجم الخط: ${(typoConfig.fontSizeScale * 100).toInt()}%", fontWeight = FontWeight.SemiBold)
                Slider(
                    value = typoConfig.fontSizeScale,
                    onValueChange = { typoConfig = typoConfig.copy(fontSizeScale = it) },
                    valueRange = 0.8f..1.35f,
                    steps = 10
                )
            }

            // Text Effects Toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("خط عريض افتراضياً (Bold)", fontWeight = FontWeight.SemiBold)
                Switch(
                    checked = typoConfig.fontWeightBold,
                    onCheckedChange = { typoConfig = typoConfig.copy(fontWeightBold = it) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("توهج النصوص (Text Glow)", fontWeight = FontWeight.SemiBold)
                Switch(
                    checked = typoConfig.hasGlow,
                    onCheckedChange = { typoConfig = typoConfig.copy(hasGlow = it) }
                )
            }

            Button(
                onClick = {
                    customizationManager.updateActiveTheme {
                        it.copy(
                            primaryColorHex = primaryHex,
                            accentColorHex = accentHex,
                            backgroundColorHex = bgHex,
                            typography = typoConfig
                        )
                    }
                    Toast.makeText(context, "تم حفظ الإعدادات بنجاح!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("تطبيق التعديلات")
            }
        }
    }
}
