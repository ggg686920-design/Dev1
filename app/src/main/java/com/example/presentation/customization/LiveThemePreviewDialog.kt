package com.example.presentation.customization

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.core.customization.*

@Composable
fun LiveThemePreviewDialog(
    theme: ThemeConfig,
    onDismiss: () -> Unit,
    onApply: () -> Unit
) {
    val wallpaperItem = ThemePresets.builtInWallpapers.find { it.id == theme.wallpaperId }
        ?: ThemePresets.builtInWallpapers.first()

    val primary = parseHexColor(theme.primaryColorHex, Color(0xFF0284C7))
    val bg = parseHexColor(theme.backgroundColorHex, Color(0xFF0B1120))
    val surface = parseHexColor(theme.surfaceColorHex, Color(0xFF111827))
    val text = parseHexColor(theme.textColorHex, Color.White)
    val textSec = parseHexColor(theme.textSecondaryHex, Color.Gray)
    val bubble = theme.bubbleConfig

    val incBg = parseHexColor(bubble.incomingBgHex, Color(0xFF1E293B))
    val outBg = parseHexColor(bubble.outgoingBgHex, primary)
    val incText = parseHexColor(bubble.incomingTextHex, Color.White)
    val outText = parseHexColor(bubble.outgoingTextHex, Color.White)
    val timeColor = parseHexColor(bubble.timestampColorHex, textSec)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = bg
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header in Dialog
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(surface)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = text)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "معاينة حية: ${theme.name}",
                                color = text,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "${theme.category} • ${if (theme.isDark) "داكن" else "فاتح"}",
                                color = textSec,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Button(
                        onClick = onApply,
                        colors = ButtonDefaults.buttonColors(containerColor = primary),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تطبيق الثيم", fontWeight = FontWeight.Bold)
                    }
                }

                // Chat Mock Area with Wallpaper
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    WallpaperRenderer(wallpaperItem = wallpaperItem) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(bubble.spacingDp.dp)
                        ) {
                            // Top Date badge
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Surface(
                                    color = surface.copy(alpha = 0.85f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "اليوم • محادثة تجريبية",
                                        color = textSec,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Incoming Bubble
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(primary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("D", color = primary, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .widthIn(max = 280.dp)
                                        .then(
                                            if (bubble.hasShadow) Modifier.shadow(3.dp, RoundedCornerShape(bubble.radiusDp.dp)) else Modifier
                                        )
                                        .clip(RoundedCornerShape(bubble.radiusDp.dp))
                                        .background(incBg.copy(alpha = bubble.transparency))
                                        .then(
                                            if (bubble.hasBorder) Modifier.border(bubble.borderWidthDp.dp, parseHexColor(bubble.borderColorHex), RoundedCornerShape(bubble.radiusDp.dp)) else Modifier
                                        )
                                        .padding(bubble.paddingDp.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "مرحباً بك في dev! هذا شكل فقاعة المحادثة الواردة في هذا الثيم.",
                                            color = incText,
                                            fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "10:42 ص",
                                            color = timeColor,
                                            fontSize = 10.sp,
                                            modifier = Modifier.align(Alignment.End)
                                        )
                                    }
                                }
                            }

                            // Outgoing Bubble
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Box(
                                    modifier = Modifier
                                        .widthIn(max = 280.dp)
                                        .then(
                                            if (bubble.hasShadow) Modifier.shadow(3.dp, RoundedCornerShape(bubble.radiusDp.dp)) else Modifier
                                        )
                                        .clip(RoundedCornerShape(bubble.radiusDp.dp))
                                        .background(outBg.copy(alpha = bubble.transparency))
                                        .then(
                                            if (bubble.hasBorder) Modifier.border(bubble.borderWidthDp.dp, parseHexColor(bubble.borderColorHex), RoundedCornerShape(bubble.radiusDp.dp)) else Modifier
                                        )
                                        .padding(bubble.paddingDp.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "تصميم فائق التخصيص والأناقة! ألوان متناسقة وعصرية.",
                                            color = outText,
                                            fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.align(Alignment.End),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "10:43 ص",
                                                color = outText.copy(alpha = 0.8f),
                                                fontSize = 10.sp
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("✓✓", color = outText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Mock Input Bar
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = surface,
                    tonalElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = when (theme.inputBarStyle) {
                                InputBarStyle.PILL -> RoundedCornerShape(24.dp)
                                InputBarStyle.ROUNDED -> RoundedCornerShape(12.dp)
                                InputBarStyle.MINIMAL, InputBarStyle.COMPACT -> RoundedCornerShape(4.dp)
                                else -> RoundedCornerShape(18.dp)
                            },
                            color = parseHexColor(theme.surfaceVariantColorHex, Color(0xFF1E293B))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ThemedIcon(AppIconAction.EMOJI, theme.iconStyle, tint = textSec, size = 20.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("اكتب رسالة...", color = textSec, fontSize = 14.sp, modifier = Modifier.weight(1f))
                                ThemedIcon(AppIconAction.ATTACHMENT, theme.iconStyle, tint = textSec, size = 20.dp)
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}
