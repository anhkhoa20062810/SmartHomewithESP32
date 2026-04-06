package com.example.esp32_led.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val SmartHomeDarkColors = darkColorScheme(
    primary          = AccentBlue,
    onPrimary        = TextPrimary,
    primaryContainer = DarkSurface2,
    secondary        = GreenActive,
    onSecondary      = DarkBg,
    background       = DarkBg,
    onBackground     = TextPrimary,
    surface          = DarkSurface,
    onSurface        = TextPrimary,
    surfaceVariant   = DarkSurface2,
    onSurfaceVariant = TextSecondary,
    outline          = DarkBorder,
    error            = RedAlert,
    onError          = TextPrimary,
)

@Composable
fun SmartHomeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SmartHomeDarkColors,
        typography  = SmartHomeTypography,
        content     = content
    )
}
