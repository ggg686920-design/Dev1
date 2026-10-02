package com.example.presentation.customization

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.customization.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedChatDesignerScreen(
    customizationManager: CustomizationManager,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val currentTheme by customizationManager.currentTheme.collectAsState()
    var bubble by remember { mutableStateOf(currentTheme.bubbleConfig) }
    var selectedReactionPack by remember { mutableStateOf("Default Emoji") }
    var selectedDecoration by remember { mutableStateOf(currentTheme.decorationStyle) }

    val presets = ExpandedPresets.bubblePresets
    val reactionPacks = listOf("Default Emoji", "Cosmic Glow ✨", "Gaming RGB 🎮", "Minimal Mono 🖤", "Cute Pastel 🌸")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("مصمم المحادثات المتقدم (Chat Designer)", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text("20+ قالب فقاعات، تفاعلات، وزخارف خلفية", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                                it.copy(bubbleConfig = bubble, decorationStyle = selectedDecoration)
                            }
                            Toast.makeText(context, "تم حفظ تصميم المحادثة والفقاعات بنجاح!", Toast.LENGTH_SHORT).show()
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
            // Live Interactive Preview Box
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = parseHexColor(currentTheme.surfaceColorHex)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(bubble.spacingDp.dp)
                ) {
                    Text("المعاينة المباشرة لتصميم الفقاعة", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                    // Incoming
                    Box(
                        modifier = Modifier
                            .widthIn(max = 280.dp)
                            .then(if (bubble.hasShadow) Modifier.shadow(3.dp, RoundedCornerShape(bubble.radiusDp.dp)) else Modifier)
                            .clip(RoundedCornerShape(bubble.radiusDp.dp))
                            .background(parseHexColor(bubble.incomingBgHex).copy(alpha = bubble.transparency))
                            .then(if (bubble.hasBorder) Modifier.border(bubble.borderWidthDp.dp, parseHexColor(bubble.borderColorHex), RoundedCornerShape(bubble.radiusDp.dp)) else Modifier)
                            .padding(bubble.paddingDp.dp)
                    ) {
                        Column {
                            Text("أهلاً بك! هذا نموذج فقاعة الرسالة الواردة.", color = parseHexColor(bubble.incomingTextHex), fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("10:45 ص", color = parseHexColor(bubble.timestampColorHex), fontSize = 10.sp, modifier = Modifier.align(Alignment.End))
                        }
                    }

                    // Outgoing
                    Box(
                        modifier = Modifier
                            .align(Alignment.End)
                            .widthIn(max = 280.dp)
                            .then(if (bubble.hasShadow) Modifier.shadow(3.dp, RoundedCornerShape(bubble.radiusDp.dp)) else Modifier)
                            .clip(RoundedCornerShape(bubble.radiusDp.dp))
                            .background(parseHexColor(bubble.outgoingBgHex).copy(alpha = bubble.transparency))
                            .then(if (bubble.hasBorder) Modifier.border(bubble.borderWidthDp.dp, parseHexColor(bubble.borderColorHex), RoundedCornerShape(bubble.radiusDp.dp)) else Modifier)
                            .padding(bubble.paddingDp.dp)
                    ) {
                        Column {
                            Text("تصميم فريد مع رد فعل سريع ⚡", color = parseHexColor(bubble.outgoingTextHex), fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(modifier = Modifier.align(Alignment.End), verticalAlignment = Alignment.CenterVertically) {
                                Text("10:46 ص", color = parseHexColor(bubble.outgoingTextHex).copy(alpha = 0.8f), fontSize = 10.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("✓✓", color = parseHexColor(bubble.outgoingTextHex), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 20+ Bubble Presets Selector
            Text("قوالب الفقاعات الجاهزة (20+ Presets)", fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(presets) { p ->
                    FilterChip(
                        selected = bubble.radiusDp == p.config.radiusDp && bubble.shape == p.config.shape,
                        onClick = {
                            bubble = p.config.copy(
                                incomingBgHex = bubble.incomingBgHex,
                                outgoingBgHex = bubble.outgoingBgHex
                            )
                        },
                        label = { Text(p.name, fontSize = 11.sp) }
                    )
                }
            }

            // Radius Slider
            Column {
                Text("انحناء الحواف (Radius): ${bubble.radiusDp}dp", fontWeight = FontWeight.SemiBold)
                Slider(
                    value = bubble.radiusDp.toFloat(),
                    onValueChange = { bubble = bubble.copy(radiusDp = it.toInt()) },
                    valueRange = 0f..32f,
                    steps = 31
                )
            }

            // Padding Slider
            Column {
                Text("المسافة الداخلية (Padding): ${bubble.paddingDp}dp", fontWeight = FontWeight.SemiBold)
                Slider(
                    value = bubble.paddingDp.toFloat(),
                    onValueChange = { bubble = bubble.copy(paddingDp = it.toInt()) },
                    valueRange = 6f..24f,
                    steps = 17
                )
            }

            // Transparency Slider
            Column {
                Text("الشفافية (Transparency): ${(bubble.transparency * 100).toInt()}%", fontWeight = FontWeight.SemiBold)
                Slider(
                    value = bubble.transparency,
                    onValueChange = { bubble = bubble.copy(transparency = it) },
                    valueRange = 0.5f..1.0f
                )
            }

            // Border Width Slider
            Column {
                Text("عرض الحد الخارجي: ${bubble.borderWidthDp}dp", fontWeight = FontWeight.SemiBold)
                Slider(
                    value = bubble.borderWidthDp.toFloat(),
                    onValueChange = { bubble = bubble.copy(borderWidthDp = it.toInt(), hasBorder = it > 0) },
                    valueRange = 0f..4f,
                    steps = 3
                )
            }

            // Toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("ظلال الفقاعات (Drop Shadow)", fontWeight = FontWeight.SemiBold)
                Switch(checked = bubble.hasShadow, onCheckedChange = { bubble = bubble.copy(hasShadow = it) })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("ذيل الفقاعة (Chat Tail)", fontWeight = FontWeight.SemiBold)
                Switch(checked = bubble.hasTail, onCheckedChange = { bubble = bubble.copy(hasTail = it) })
            }

            HorizontalDivider()

            // Reaction Packs
            Text("حزم التفاعلات السريعة (Reaction Packs)", fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(reactionPacks) { pack ->
                    FilterChip(
                        selected = selectedReactionPack == pack,
                        onClick = { selectedReactionPack = pack },
                        label = { Text(pack) }
                    )
                }
            }

            HorizontalDivider()

            // Decorations Selector
            Text("زخارف المحادثة (Decorations)", fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(DecorationStyle.entries) { decor ->
                    FilterChip(
                        selected = selectedDecoration == decor,
                        onClick = { selectedDecoration = decor },
                        label = { Text(decor.titleAr, fontSize = 11.sp) }
                    )
                }
            }

            Button(
                onClick = {
                    customizationManager.updateActiveTheme {
                        it.copy(bubbleConfig = bubble, decorationStyle = selectedDecoration)
                    }
                    Toast.makeText(context, "تم تطبيق تصميم المحادثة!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("تطبيق التعديلات الحالية")
            }
        }
    }
}
