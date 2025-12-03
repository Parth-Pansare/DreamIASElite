package com.app.dreamiaselite.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

private val LightColorScheme = lightColorScheme(
    primary = Gold,
    onPrimary = LightSurface,
    secondary = AccentCyan,
    background = LightBackground,
    surface = LightSurface,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

private val DarkColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = DarkSurface,
    secondary = AccentCyan,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary
)

data class ThemeController(
    val isDarkMode: Boolean,
    val setDarkMode: (Boolean) -> Unit
)

val LocalThemeController = staticCompositionLocalOf {
    ThemeController(isDarkMode = false, setDarkMode = {})
}

@Composable
fun DreamIasEliteTheme(
    isDarkMode: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (isDarkMode) DarkColorScheme else LightColorScheme,
        typography = AppTypography,
        content = content
    )
}
