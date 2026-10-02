package com.example.presentation.customization

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.customization.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenDesignerScreen(
    customizationManager: CustomizationManager,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val currentTheme by customizationManager.currentTheme.collectAsState()

    var homeConfig by remember { mutableStateOf(HomeScreenDesignConfig(cardRadiusDp = currentTheme.cardRadiusDp)) }
    var navStyle by remember { mutableStateOf(currentTheme.navBarStyle) }
    var inputStyle by remember { mutableStateOf(currentTheme.inputBarStyle) }
    var btnStyle by remember { mutableStateOf(currentTheme.buttonStyle) }

    val layouts = HomeLayoutPreset.entries

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("مصمم الشاشة الرئيسية والواجهة", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text("تخطيطات الهوم، شريط التنقل، والأزرار والبطاقات", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                                    cardRadiusDp = homeConfig.cardRadiusDp,
                                    navBarStyle = navStyle,
                                    inputBarStyle = inputStyle,
                                    buttonStyle = btnStyle
                                )
                            }
                            Toast.makeText(context, "تم حفظ تخطيطات الشاشة الرئيسية بنجاح!", Toast.LENGTH_SHORT).show()
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
            // Home Layout Presets
            Text("نمط تخطيط الصفحة الرئيسية (Layout Presets)", fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(layouts) { layout ->
                    FilterChip(
                        selected = homeConfig.layoutPreset == layout,
                        onClick = { homeConfig = homeConfig.copy(layoutPreset = layout) },
                        label = { Text(layout.titleAr, fontSize = 11.sp) }
                    )
                }
            }

            // Card Radius Slider
            Column {
                Text("انحناء بطاقات المحادثة: ${homeConfig.cardRadiusDp}dp", fontWeight = FontWeight.SemiBold)
                Slider(
                    value = homeConfig.cardRadiusDp.toFloat(),
                    onValueChange = { homeConfig = homeConfig.copy(cardRadiusDp = it.toInt()) },
                    valueRange = 0f..28f,
                    steps = 27
                )
            }

            // Avatar Size Slider
            Column {
                Text("حجم صورة الحساب في القائمة: ${homeConfig.avatarSizeDp}dp", fontWeight = FontWeight.SemiBold)
                Slider(
                    value = homeConfig.avatarSizeDp.toFloat(),
                    onValueChange = { homeConfig = homeConfig.copy(avatarSizeDp = it.toInt()) },
                    valueRange = 40f..68f,
                    steps = 27
                )
            }

            // Toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("إظهار شارة الاتصال الأخضر (Online Badge)", fontWeight = FontWeight.SemiBold)
                Switch(
                    checked = homeConfig.showOnlineIndicatorBadge,
                    onCheckedChange = { homeConfig = homeConfig.copy(showOnlineIndicatorBadge = it) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("صورة الحساب دائرية (Circular Avatar)", fontWeight = FontWeight.SemiBold)
                Switch(
                    checked = homeConfig.isAvatarCircular,
                    onCheckedChange = { homeConfig = homeConfig.copy(isAvatarCircular = it) }
                )
            }

            HorizontalDivider()

            // Bottom Navigation Style
            Text("شريط التنقل السفلي (Bottom Navigation)", fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(NavBarStyle.entries) { style ->
                    FilterChip(
                        selected = navStyle == style,
                        onClick = { navStyle = style },
                        label = { Text(style.titleAr, fontSize = 11.sp) }
                    )
                }
            }

            HorizontalDivider()

            // Input Bar Style
            Text("شريط إدخال الرسائل (Input Bar)", fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(InputBarStyle.entries) { style ->
                    FilterChip(
                        selected = inputStyle == style,
                        onClick = { inputStyle = style },
                        label = { Text(style.titleAr, fontSize = 11.sp) }
                    )
                }
            }

            HorizontalDivider()

            // Button Style
            Text("نمط الأزرار (Buttons)", fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(ButtonStyle.entries) { style ->
                    FilterChip(
                        selected = btnStyle == style,
                        onClick = { btnStyle = style },
                        label = { Text(style.titleAr, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    customizationManager.updateActiveTheme {
                        it.copy(
                            cardRadiusDp = homeConfig.cardRadiusDp,
                            navBarStyle = navStyle,
                            inputBarStyle = inputStyle,
                            buttonStyle = btnStyle
                        )
                    }
                    Toast.makeText(context, "تم تطبيق إعدادات الواجهة!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("تطبيق جميع الإعدادات")
            }
        }
    }
}
