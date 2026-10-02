package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.core.settings.*

val LocalAppSettings = staticCompositionLocalOf { AppSettings() }
val LocalBubbleRadius = staticCompositionLocalOf { 16.dp }
val LocalChatWallpaper = staticCompositionLocalOf { WallpaperPreset.DEFAULT }

@Composable
fun RaseelTheme(
    appSettings: AppSettings,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val isDark = when (appSettings.themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }

    val primaryColor = if (isDark) appSettings.accentColor.darkColor else appSettings.accentColor.lightColor
    val primaryContainer = if (isDark) primaryColor.copy(alpha = 0.25f) else primaryColor.copy(alpha = 0.15f)

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = primaryColor,
            onPrimary = Color.White,
            primaryContainer = primaryContainer,
            onPrimaryContainer = Color.White,
            secondary = SlateDarkSecondary,
            onSecondary = SlateDarkOnSecondary,
            secondaryContainer = SlateDarkSecondaryContainer,
            onSecondaryContainer = SlateDarkOnSecondaryContainer,
            background = DarkBackground,
            surface = DarkSurface,
            surfaceVariant = DarkSurfaceVariant,
            onBackground = DarkOnSurface,
            onSurface = DarkOnSurface,
            onSurfaceVariant = DarkOnSurfaceVariant
        )
    } else {
        lightColorScheme(
            primary = primaryColor,
            onPrimary = Color.White,
            primaryContainer = primaryContainer,
            onPrimaryContainer = primaryColor,
            secondary = SlateSecondary,
            onSecondary = SlateOnSecondary,
            secondaryContainer = SlateSecondaryContainer,
            onSecondaryContainer = SlateOnSecondaryContainer,
            background = LightBackground,
            surface = LightSurface,
            surfaceVariant = LightSurfaceVariant,
            onBackground = LightOnSurface,
            onSurface = LightOnSurface,
            onSurfaceVariant = LightOnSurfaceVariant
        )
    }

    val scale = appSettings.fontSizeScale.scale
    val scaledTypography = Typography(
        displayLarge = Typography.displayLarge.scale(scale),
        displayMedium = Typography.displayMedium.scale(scale),
        displaySmall = Typography.displaySmall.scale(scale),
        headlineLarge = Typography.headlineLarge.scale(scale),
        headlineMedium = Typography.headlineMedium.scale(scale),
        headlineSmall = Typography.headlineSmall.scale(scale),
        titleLarge = Typography.titleLarge.scale(scale),
        titleMedium = Typography.titleMedium.scale(scale),
        titleSmall = Typography.titleSmall.scale(scale),
        bodyLarge = Typography.bodyLarge.scale(scale),
        bodyMedium = Typography.bodyMedium.scale(scale),
        bodySmall = Typography.bodySmall.scale(scale),
        labelLarge = Typography.labelLarge.scale(scale),
        labelMedium = Typography.labelMedium.scale(scale),
        labelSmall = Typography.labelSmall.scale(scale)
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
        }
    }

    CompositionLocalProvider(
        LocalAppSettings provides appSettings,
        LocalBubbleRadius provides appSettings.bubbleRadius.radiusDp.dp,
        LocalChatWallpaper provides appSettings.wallpaperPreset
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = scaledTypography,
            content = content
        )
    }
}

private fun androidx.compose.ui.text.TextStyle.scale(factor: Float): androidx.compose.ui.text.TextStyle {
    return this.copy(
        fontSize = if (this.fontSize != TextUnit.Unspecified) (this.fontSize.value * factor).sp else this.fontSize,
        lineHeight = if (this.lineHeight != TextUnit.Unspecified) (this.lineHeight.value * factor).sp else this.lineHeight
    )
}
