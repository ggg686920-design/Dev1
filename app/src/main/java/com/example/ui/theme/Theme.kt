package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.core.customization.*
import com.example.core.settings.AppSettings

val LocalAppSettings = staticCompositionLocalOf { AppSettings() }
val LocalBubbleRadius = staticCompositionLocalOf { 16.dp }
val LocalThemeConfig = staticCompositionLocalOf { ThemePresets.builtInThemes.first() }
val LocalBubbleConfig = staticCompositionLocalOf { BubbleConfig() }
val LocalIconStyle = staticCompositionLocalOf { IconStyle.ROUNDED }
val LocalEffectsConfig = staticCompositionLocalOf { VisualEffectsConfig() }

@Composable
fun RaseelTheme(
    appSettings: AppSettings,
    themeConfig: ThemeConfig = ThemePresets.builtInThemes.first(),
    content: @Composable () -> Unit
) {
    val isDark = themeConfig.isDark

    val primaryColor = parseHexColor(themeConfig.primaryColorHex, Color(0xFF0284C7))
    val secondaryColor = parseHexColor(themeConfig.secondaryColorHex, Color(0xFF0F172A))
    val backgroundColor = parseHexColor(themeConfig.backgroundColorHex, if (isDark) Color(0xFF0B1120) else Color(0xFFFFFFFF))
    val surfaceColor = parseHexColor(themeConfig.surfaceColorHex, if (isDark) Color(0xFF111827) else Color(0xFFF8FAFC))
    val surfaceVariantColor = parseHexColor(themeConfig.surfaceVariantColorHex, if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9))
    val textColor = parseHexColor(themeConfig.textColorHex, if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A))
    val textSecondaryColor = parseHexColor(themeConfig.textSecondaryHex, if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B))

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = primaryColor,
            onPrimary = Color.White,
            primaryContainer = primaryColor.copy(alpha = 0.25f),
            onPrimaryContainer = Color.White,
            secondary = secondaryColor,
            onSecondary = textColor,
            secondaryContainer = surfaceVariantColor,
            onSecondaryContainer = textColor,
            background = backgroundColor,
            surface = surfaceColor,
            surfaceVariant = surfaceVariantColor,
            onBackground = textColor,
            onSurface = textColor,
            onSurfaceVariant = textSecondaryColor
        )
    } else {
        lightColorScheme(
            primary = primaryColor,
            onPrimary = Color.White,
            primaryContainer = primaryColor.copy(alpha = 0.15f),
            onPrimaryContainer = primaryColor,
            secondary = secondaryColor,
            onSecondary = textColor,
            secondaryContainer = surfaceVariantColor,
            onSecondaryContainer = textColor,
            background = backgroundColor,
            surface = surfaceColor,
            surfaceVariant = surfaceVariantColor,
            onBackground = textColor,
            onSurface = textColor,
            onSurfaceVariant = textSecondaryColor
        )
    }

    val baseFontFamily = when (themeConfig.typography.fontFamilyType) {
        FontFamilyType.SANS_SERIF -> FontFamily.SansSerif
        FontFamilyType.SERIF -> FontFamily.Serif
        FontFamilyType.MONOSPACE -> FontFamily.Monospace
        FontFamilyType.MODERN_ROUNDED -> FontFamily.Default
        FontFamilyType.TECH_CODE -> FontFamily.Monospace
        FontFamilyType.SYSTEM -> FontFamily.Default
    }

    val scale = themeConfig.typography.fontSizeScale * appSettings.fontSizeScale.scale
    val scaledTypography = Typography(
        displayLarge = Typography.displayLarge.scale(scale, baseFontFamily),
        displayMedium = Typography.displayMedium.scale(scale, baseFontFamily),
        displaySmall = Typography.displaySmall.scale(scale, baseFontFamily),
        headlineLarge = Typography.headlineLarge.scale(scale, baseFontFamily),
        headlineMedium = Typography.headlineMedium.scale(scale, baseFontFamily),
        headlineSmall = Typography.headlineSmall.scale(scale, baseFontFamily),
        titleLarge = Typography.titleLarge.scale(scale, baseFontFamily),
        titleMedium = Typography.titleMedium.scale(scale, baseFontFamily),
        titleSmall = Typography.titleSmall.scale(scale, baseFontFamily),
        bodyLarge = Typography.bodyLarge.scale(scale, baseFontFamily),
        bodyMedium = Typography.bodyMedium.scale(scale, baseFontFamily),
        bodySmall = Typography.bodySmall.scale(scale, baseFontFamily),
        labelLarge = Typography.labelLarge.scale(scale, baseFontFamily),
        labelMedium = Typography.labelMedium.scale(scale, baseFontFamily),
        labelSmall = Typography.labelSmall.scale(scale, baseFontFamily)
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = surfaceColor.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
        }
    }

    CompositionLocalProvider(
        LocalAppSettings provides appSettings,
        LocalThemeConfig provides themeConfig,
        LocalBubbleRadius provides themeConfig.bubbleConfig.radiusDp.dp,
        LocalBubbleConfig provides themeConfig.bubbleConfig,
        LocalIconStyle provides themeConfig.iconStyle,
        LocalEffectsConfig provides themeConfig.effects
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = scaledTypography,
            content = content
        )
    }
}

private fun androidx.compose.ui.text.TextStyle.scale(factor: Float, fontFamily: FontFamily): androidx.compose.ui.text.TextStyle {
    return this.copy(
        fontFamily = fontFamily,
        fontSize = if (this.fontSize != TextUnit.Unspecified) (this.fontSize.value * factor).sp else this.fontSize,
        lineHeight = if (this.lineHeight != TextUnit.Unspecified) (this.lineHeight.value * factor).sp else this.lineHeight
    )
}
