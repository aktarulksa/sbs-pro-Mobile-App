package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun MyApplicationTheme(
    themeOption: SbsThemeOption = SbsThemeOption.PURPLE_CLASSIC,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val darkColorScheme = darkColorScheme(
        primary = themeOption.accent,
        onPrimary = Color.Black,
        primaryContainer = themeOption.darkSurface,
        onPrimaryContainer = Color.White,
        secondary = themeOption.accent,
        background = Color(0xFF141218),
        surface = Color(0xFF1E1B24),
        onBackground = Color.White,
        onSurface = Color.White,
        outline = Color(0xFF333333)
    )

    val lightColorScheme = lightColorScheme(
        primary = themeOption.primary,
        onPrimary = Color.White,
        primaryContainer = themeOption.container,
        onPrimaryContainer = themeOption.primary,
        secondary = themeOption.accent,
        onSecondary = Color.White,
        background = SbsBackground,
        surface = SbsSurface,
        onBackground = SbsTextPrimary,
        onSurface = SbsTextPrimary,
        outline = SbsDivider
    )

    val colorScheme = if (darkTheme) darkColorScheme else lightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
