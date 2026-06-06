package com.antigravity.distractionshield.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Immutable
data class ThemeConfig(
    val isDark: Boolean = false,
    val glassBase: Color = Color(0x0CFFFFFF),
    val glassBorderLight: Color = Color(0xCCFFFFFF),
    val glassBorderDark: Color = Color(0x1F000000),
    val auroraBlobColors: List<Color> = emptyList()
)

val LocalThemeConfig = staticCompositionLocalOf { ThemeConfig() }

private val CustomLightColorScheme = lightColorScheme(
    primary = NeonCyan,
    secondary = NeonPurple,
    tertiary = NeonPink,
    background = ObsidianBg,
    surface = ObsidianSurface,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    onSurfaceVariant = LightTextSecondary,
    primaryContainer = ObsidianSurface,
    onPrimaryContainer = LightTextPrimary
)

private val CustomDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    secondary = NeonPurple,
    tertiary = NeonPink,
    background = ObsidianBgDark,
    surface = ObsidianSurfaceDark,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    onSurfaceVariant = DarkTextSecondary,
    primaryContainer = ObsidianSurfaceDark,
    onPrimaryContainer = DarkTextPrimary
)

@Composable
fun DistractionBlockerTheme(
    themeMode: String = "SYSTEM",
    useDynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        "LIGHT" -> false
        "DARK" -> true
        else -> isSystemInDarkTheme()
    }

    val context = LocalContext.current
    val colorScheme = when {
        useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> CustomDarkColorScheme
        else -> CustomLightColorScheme
    }

    // Custom visuals config (aurora colors & glass card transparency/bevel opacity)
    val themeConfig = if (darkTheme) {
        ThemeConfig(
            isDark = true,
            glassBase = Color(0x08FFFFFF), // more transparent in dark mode
            glassBorderLight = Color(0x1AFFFFFF), // soft highlights (10% white) to prevent glare
            glassBorderDark = Color(0x4D000000), // deep shadow border (30% black) for contrast
            auroraBlobColors = if (useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                listOf(
                    colorScheme.primary.copy(alpha = 0.35f),
                    colorScheme.secondary.copy(alpha = 0.35f),
                    colorScheme.tertiary.copy(alpha = 0.35f),
                    colorScheme.primaryContainer.copy(alpha = 0.35f),
                    colorScheme.secondaryContainer.copy(alpha = 0.35f)
                )
            } else {
                listOf(
                    GradientGreen.copy(alpha = 0.3f),
                    GradientYellow.copy(alpha = 0.3f),
                    GradientRed.copy(alpha = 0.3f),
                    GradientPink.copy(alpha = 0.3f),
                    GradientMagenta.copy(alpha = 0.3f)
                )
            }
        )
    } else {
        ThemeConfig(
            isDark = false,
            glassBase = Color(0x0CFFFFFF), // 5% white base
            glassBorderLight = Color(0xCCFFFFFF), // high bevel highlights (80% white)
            glassBorderDark = Color(0x1F000000), // soft shadow border (12% black)
            auroraBlobColors = if (useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                listOf(
                    colorScheme.primary.copy(alpha = 0.55f),
                    colorScheme.secondary.copy(alpha = 0.55f),
                    colorScheme.tertiary.copy(alpha = 0.55f),
                    colorScheme.primaryContainer.copy(alpha = 0.55f),
                    colorScheme.secondaryContainer.copy(alpha = 0.55f)
                )
            } else {
                listOf(
                    GradientGreen.copy(alpha = 0.55f),
                    GradientYellow.copy(alpha = 0.6f),
                    GradientRed.copy(alpha = 0.55f),
                    GradientPink.copy(alpha = 0.6f),
                    GradientMagenta.copy(alpha = 0.55f)
                )
            }
        )
    }

    CompositionLocalProvider(LocalThemeConfig provides themeConfig) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
