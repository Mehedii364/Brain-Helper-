package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val ObsidianDarkColorScheme = darkColorScheme(
    primary = VioletPrimary,
    onPrimary = Color.White,
    primaryContainer = ObsidianSecondarySurface,
    onPrimaryContainer = VioletHighlight,
    secondary = ElectricBlue,
    onSecondary = ObsidianBackground,
    secondaryContainer = ObsidianSecondarySurface,
    onSecondaryContainer = ElectricBlue,
    tertiary = MintSuccess,
    onTertiary = ObsidianBackground,
    background = ObsidianBackground,
    onBackground = TextPrimary,
    surface = ObsidianSurface,
    onSurface = TextPrimary,
    surfaceVariant = ObsidianSecondarySurface,
    onSurfaceVariant = TextSecondary,
    outline = ObsidianBorder,
    outlineVariant = ObsidianBorder,
    error = ErrorColor,
    onError = Color.White
)

val AmoledPitchBlackColorScheme = darkColorScheme(
    primary = VioletPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF141620),
    onPrimaryContainer = VioletHighlight,
    secondary = ElectricBlue,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF141620),
    onSecondaryContainer = ElectricBlue,
    tertiary = MintSuccess,
    onTertiary = Color.Black,
    background = Color.Black,
    onBackground = TextPrimary,
    surface = Color(0xFF0D0F14),
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFF161922),
    onSurfaceVariant = TextSecondary,
    outline = Color(0xFF222636),
    outlineVariant = Color(0xFF222636),
    error = ErrorColor,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    isAmoled: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (isAmoled) AmoledPitchBlackColorScheme else ObsidianDarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
