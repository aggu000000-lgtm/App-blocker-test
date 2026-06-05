package com.antigravity.distractionshield.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CustomDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    secondary = NeonPurple,
    tertiary = NeonPink,
    background = ObsidianBg,
    surface = ObsidianSurface,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    primaryContainer = ObsidianSurface,
    onPrimaryContainer = TextPrimary
)

@Composable
fun DistractionBlockerTheme(
    content: @Composable () -> Unit,
) {
    // Force our beautiful custom dark color scheme for glassmorphism
    MaterialTheme(
        colorScheme = CustomDarkColorScheme,
        typography = Typography,
        content = content
    )
}
