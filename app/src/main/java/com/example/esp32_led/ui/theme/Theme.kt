package com.example.esp32_led.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SmartHomeDark = darkColorScheme(
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

private val SmartHomeLight = lightColorScheme(
    primary          = AccentBlue,
    onPrimary        = LightBg,
    primaryContainer = LightSurface2,
    secondary        = GreenActive,
    onSecondary      = LightBg,
    background       = LightBg,
    onBackground     = TextPrimaryL,
    surface          = LightSurface,
    onSurface        = TextPrimaryL,
    surfaceVariant   = LightSurface2,
    onSurfaceVariant = TextSecondaryL,
    outline          = LightBorder,
    error            = RedAlert,
    onError          = LightSurface,
)

@Composable
fun SmartHomeTheme(
    darkMode: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkMode) SmartHomeDark else SmartHomeLight,
        typography  = SmartHomeTypography,
        content     = content
    )
}
