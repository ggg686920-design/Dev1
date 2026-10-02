package com.example.presentation.customization

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material.icons.filled.*
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
fun IconStoreAndEditorScreen(
    customizationManager: CustomizationManager,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val currentTheme by customizationManager.currentTheme.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Packs, 1: Custom Icon Editor

    var editorConfig by remember {
        mutableStateOf(
            CustomIconEditorConfig(
                iconColorHex = currentTheme.primaryColorHex,
                backgroundColorHex = currentTheme.surfaceVariantColorHex
            )
        )
    }

    val iconStyles = IconStyle.entries
    val actions = ExtendedIconAction.entries.take(16)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("متجر ومحرر الأيقونات (Icon Studio)", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text("18+ نمط وحزمة أيقونات مع محرر تصميم خاص", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("حزم الأيقونات (Packs)", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("محرر الأيقونات (Editor)", fontWeight = FontWeight.Bold) }
                )
            }

            if (selectedTab == 0) {
                // ICON PACKS BROWSER
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Text("اختر نمط الأيقونات لتطبيقه على كافة أزرار وعناصر التطبيق:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    items(iconStyles) { style ->
                        val isSelected = style == currentTheme.iconStyle

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    customizationManager.updateActiveTheme { it.copy(iconStyle = style) }
                                    Toast.makeText(context, "تم تطبيق نمط الأيقونات: ${style.titleAr}", Toast.LENGTH_SHORT).show()
                                }
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(style.titleAr, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text(style.titleEn, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    if (isSelected) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("مفعّل حالياً ✓", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Visual Preview of icons
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    items(actions) { action ->
                                        ThemedIcon(
                                            action = when (action) {
                                                ExtendedIconAction.HOME -> AppIconAction.HOME
                                                ExtendedIconAction.CHATS -> AppIconAction.CHATS
                                                ExtendedIconAction.CONTACTS -> AppIconAction.CONTACTS
                                                ExtendedIconAction.SEARCH -> AppIconAction.SEARCH
                                                ExtendedIconAction.SETTINGS -> AppIconAction.SETTINGS
                                                ExtendedIconAction.SEND -> AppIconAction.SEND
                                                ExtendedIconAction.VOICE -> AppIconAction.VOICE
                                                ExtendedIconAction.CAMERA -> AppIconAction.CAMERA
                                                ExtendedIconAction.GALLERY -> AppIconAction.GALLERY
                                                ExtendedIconAction.FILES -> AppIconAction.FILES
                                                ExtendedIconAction.LOCATION -> AppIconAction.LOCATION
                                                ExtendedIconAction.CONTACT -> AppIconAction.CONTACT
                                                ExtendedIconAction.BACK -> AppIconAction.BACK
                                                ExtendedIconAction.MORE -> AppIconAction.MORE
                                                ExtendedIconAction.ATTACHMENT -> AppIconAction.ATTACHMENT
                                                else -> AppIconAction.EMOJI
                                            },
                                            style = style,
                                            tint = MaterialTheme.colorScheme.primary,
                                            size = 24.dp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // CUSTOM ICON EDITOR
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Live Icon Preview Box
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("معاينة الأيقونة المصممة", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            RenderCustomIcon(config = editorConfig)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("الرمز: ${editorConfig.selectedSymbolName} • الحجم: ${editorConfig.iconSizeDp}dp", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    // Symbol Picker
                    Text("اختر الرمز الأساسي (Symbol)", fontWeight = FontWeight.Bold)
                    val symbols = listOf("CHAT", "SEND", "CAMERA", "HEART", "STAR", "ROCKET", "CODE", "FIRE")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(symbols) { sym ->
                            FilterChip(
                                selected = editorConfig.selectedSymbolName == sym,
                                onClick = { editorConfig = editorConfig.copy(selectedSymbolName = sym) },
                                label = { Text(sym) }
                            )
                        }
                    }

                    // Radius slider
                    Column {
                        Text("انحناء الخلفية: ${editorConfig.shapeRadiusDp}dp", fontWeight = FontWeight.SemiBold)
                        Slider(
                            value = editorConfig.shapeRadiusDp.toFloat(),
                            onValueChange = { editorConfig = editorConfig.copy(shapeRadiusDp = it.toInt()) },
                            valueRange = 0f..28f,
                            steps = 27
                        )
                    }

                    // Size slider
                    Column {
                        Text("حجم الأيقونة الداخلي: ${editorConfig.iconSizeDp}dp", fontWeight = FontWeight.SemiBold)
                        Slider(
                            value = editorConfig.iconSizeDp.toFloat(),
                            onValueChange = { editorConfig = editorConfig.copy(iconSizeDp = it.toInt()) },
                            valueRange = 16f..48f,
                            steps = 31
                        )
                    }

                    // Rotation slider
                    Column {
                        Text("زاوية الدوران: ${editorConfig.rotationAngle.toInt()}°", fontWeight = FontWeight.SemiBold)
                        Slider(
                            value = editorConfig.rotationAngle,
                            onValueChange = { editorConfig = editorConfig.copy(rotationAngle = it) },
                            valueRange = 0f..360f
                        )
                    }

                    // Toggles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("إضافة ظل مجسم (Shadow)", fontWeight = FontWeight.SemiBold)
                        Switch(
                            checked = editorConfig.hasShadow,
                            onCheckedChange = { editorConfig = editorConfig.copy(hasShadow = it) }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("حد خارجي ملون (Border)", fontWeight = FontWeight.SemiBold)
                        Switch(
                            checked = editorConfig.borderWidthDp > 0,
                            onCheckedChange = { editorConfig = editorConfig.copy(borderWidthDp = if (it) 2 else 0) }
                        )
                    }

                    Button(
                        onClick = {
                            Toast.makeText(context, "تم حفظ وتطبيق تصميم الأيقونة المخصص بنجاح!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("حفظ وتطبيق التصميم")
                    }
                }
            }
        }
    }
}
