package com.example.core.customization

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WallpaperRenderer(
    wallpaperItem: WallpaperItem,
    modifier: Modifier = Modifier,
    reduceMotion: Boolean = false,
    content: @Composable () -> Unit = {}
) {
    val primary = parseHexColor(wallpaperItem.primaryHex, Color(0xFF0F172A))
    val secondary = parseHexColor(wallpaperItem.secondaryHex, Color(0xFF1E293B))
    val accent = parseHexColor(wallpaperItem.accentHex, Color(0xFF38BDF8))

    val transition = rememberInfiniteTransition(label = "wallpaper_anim")
    val animOffset by if (!reduceMotion && (wallpaperItem.isVideoLoop || wallpaperItem.patternType == "CIRCUITS" || wallpaperItem.patternType == "STARS")) {
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 15000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "anim_offset"
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            when (wallpaperItem.patternType) {
                "SOLID" -> {
                    drawRect(color = primary)
                }
                "LINEAR_GRADIENT" -> {
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(primary, secondary),
                            start = Offset(0f, 0f),
                            end = Offset(width, height)
                        )
                    )
                }
                "RADIAL_GRADIENT" -> {
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(secondary, primary),
                            center = Offset(width / 2, height / 3),
                            radius = width.coerceAtLeast(height) * 0.8f
                        )
                    )
                }
                "GRID" -> {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(primary, secondary)
                        )
                    )
                    // Draw cyber tech grid lines
                    val step = 48.dp.toPx()
                    var x = 0f
                    val gridColor = accent.copy(alpha = 0.08f)
                    while (x < width) {
                        drawLine(
                            color = gridColor,
                            start = Offset(x, 0f),
                            end = Offset(x, height),
                            strokeWidth = 1.dp.toPx()
                        )
                        x += step
                    }
                    var y = 0f
                    while (y < height) {
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                        y += step
                    }
                }
                "DOTS" -> {
                    drawRect(color = primary)
                    val step = 32.dp.toPx()
                    val dotColor = accent.copy(alpha = 0.12f)
                    var x = step / 2
                    while (x < width) {
                        var y = step / 2
                        while (y < height) {
                            drawCircle(
                                color = dotColor,
                                radius = 2.dp.toPx(),
                                center = Offset(x, y)
                            )
                            y += step
                        }
                        x += step
                    }
                }
                "STARS" -> {
                    drawRect(
                        brush = Brush.verticalGradient(listOf(primary, secondary))
                    )
                    // Draw starry particle field
                    val count = 40
                    for (i in 0 until count) {
                        val angle = (i * 37f + animOffset) % 360f
                        val rad = Math.toRadians(angle.toDouble())
                        val sx = ((i * 83) % width.toInt()).toFloat() + (sin(rad) * 15f).toFloat()
                        val sy = ((i * 127) % height.toInt()).toFloat() + (cos(rad) * 15f).toFloat()
                        val starRadius = if (i % 5 == 0) 3.dp.toPx() else 1.5.dp.toPx()
                        val alpha = if (i % 2 == 0) 0.6f else 0.3f
                        drawCircle(
                            color = accent.copy(alpha = alpha),
                            radius = starRadius,
                            center = Offset(sx.coerceIn(0f, width), sy.coerceIn(0f, height))
                        )
                    }
                }
                "HEXAGONS" -> {
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(secondary, primary),
                            center = Offset(width * 0.7f, height * 0.2f),
                            radius = width
                        )
                    )
                    // Hexagonal subtle overlay
                    val hexColor = accent.copy(alpha = 0.07f)
                    val r = 36.dp.toPx()
                    val h = r * kotlin.math.sqrt(3.0).toFloat()
                    var row = 0
                    var cy = 0f
                    while (cy < height + r) {
                        var cx = if (row % 2 == 0) 0f else r * 1.5f
                        while (cx < width + r) {
                            val path = Path()
                            for (k in 0..5) {
                                val deg = 60.0 * k
                                val rad = Math.toRadians(deg)
                                val px = cx + (r * cos(rad)).toFloat()
                                val py = cy + (r * sin(rad)).toFloat()
                                if (k == 0) path.moveTo(px, py) else path.lineTo(px, py)
                            }
                            path.close()
                            drawPath(path, color = hexColor, style = Stroke(width = 1.dp.toPx()))
                            cx += r * 3f
                        }
                        cy += h / 2f
                        row++
                    }
                }
                "CIRCUITS" -> {
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(primary, secondary)
                        )
                    )
                    // Tech circuit traces
                    val traceColor = accent.copy(alpha = 0.12f)
                    val p = Path()
                    p.moveTo(width * 0.1f, 0f)
                    p.lineTo(width * 0.1f, height * 0.3f)
                    p.lineTo(width * 0.3f, height * 0.45f)
                    p.lineTo(width * 0.3f, height * 0.8f)
                    drawPath(p, color = traceColor, style = Stroke(width = 2.dp.toPx()))

                    val p2 = Path()
                    p2.moveTo(width * 0.9f, 0f)
                    p2.lineTo(width * 0.9f, height * 0.2f)
                    p2.lineTo(width * 0.7f, height * 0.4f)
                    p2.lineTo(width * 0.7f, height)
                    drawPath(p2, color = traceColor, style = Stroke(width = 2.dp.toPx()))

                    drawCircle(color = accent.copy(alpha = 0.4f), radius = 5.dp.toPx(), center = Offset(width * 0.3f, height * 0.45f))
                    drawCircle(color = accent.copy(alpha = 0.4f), radius = 5.dp.toPx(), center = Offset(width * 0.7f, height * 0.4f))
                }
                "WAVES" -> {
                    drawRect(
                        brush = Brush.verticalGradient(listOf(primary, secondary))
                    )
                    val waveColor = accent.copy(alpha = 0.08f)
                    val p = Path()
                    p.moveTo(0f, height * 0.6f)
                    p.quadraticBezierTo(width * 0.25f, height * 0.5f, width * 0.5f, height * 0.6f)
                    p.quadraticBezierTo(width * 0.75f, height * 0.7f, width, height * 0.6f)
                    p.lineTo(width, height)
                    p.lineTo(0f, height)
                    p.close()
                    drawPath(p, color = waveColor)
                }
                else -> { // MESH
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(secondary, primary),
                            center = Offset(width * 0.5f, height * 0.5f),
                            radius = width
                        )
                    )
                }
            }
        }
        content()
    }
}
