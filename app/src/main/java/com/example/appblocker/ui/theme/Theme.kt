package com.example.appblocker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.foundation.LocalIndication
import androidx.compose.runtime.getValue
import com.example.appblocker.ui.foundation.BloomIndication

/**
 * Brand theme.
 *
 * Per DESIGN_NORTH_STAR.md we deliberately do NOT use Material You
 * dynamic color: the app has ONE accent app-wide. The accent itself
 * is a gradient (not a single color) and lives in [BrandColors].
 *
 * The Material color scheme below carries the neutral surfaces and
 * the muted on-surface text. Anywhere you'd reach for "primary"
 * you should instead use [BrandColors.accentGradient].
 */
private val BrandLightColors = lightColorScheme(
    primary = BrandColors.accentStart,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    background = BrandColors.lightBackground,
    onBackground = BrandColors.lightOnBackground,
    surface = BrandColors.lightBackground,
    onSurface = BrandColors.lightOnBackground,
    surfaceVariant = BrandColors.lightBackground,
    onSurfaceVariant = BrandColors.lightMuted,
    outline = BrandColors.lightMuted,
)

private val BrandDarkColors = darkColorScheme(
    primary = BrandColors.accentStart,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    background = BrandColors.darkBackground,
    onBackground = BrandColors.darkOnBackground,
    surface = BrandColors.darkBackground,
    onSurface = BrandColors.darkOnBackground,
    surfaceVariant = BrandColors.darkBackground,
    onSurfaceVariant = BrandColors.darkMuted,
    outline = BrandColors.darkMuted,
)

val LocalAuraBrush = androidx.compose.runtime.compositionLocalOf<androidx.compose.ui.graphics.Brush> {
    error("No aura brush provided")
}

@Composable
fun AppBlockerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) BrandDarkColors else BrandLightColors
    val motion = LocalMotion.current
    val isHighFidelity = com.example.appblocker.ui.foundation.LocalDynamism.current == com.example.appblocker.ui.foundation.DynamismLevel.HighFidelity
    
    val color1 by androidx.compose.animation.animateColorAsState(
        targetValue = if (isHighFidelity) androidx.compose.ui.graphics.Color(0xFF56CCF2) else BrandColors.accentStart,
        animationSpec = motion.interactiveSpringColor,
        label = "color1"
    )
    val color2 by androidx.compose.animation.animateColorAsState(
        targetValue = if (isHighFidelity) androidx.compose.ui.graphics.Color(0xFF9B51E0) else BrandColors.accentEnd,
        animationSpec = motion.interactiveSpringColor,
        label = "color2"
    )
    val color3 by androidx.compose.animation.animateColorAsState(
        targetValue = if (isHighFidelity) BrandColors.accentStart else BrandColors.accentEnd,
        animationSpec = motion.interactiveSpringColor,
        label = "color3"
    )
    
    val auraBrush = if (isHighFidelity) {
        androidx.compose.ui.graphics.Brush.linearGradient(listOf(color1, color2, color3))
    } else {
        androidx.compose.ui.graphics.Brush.linearGradient(listOf(color1, color2))
    }

    val bloomResources = androidx.compose.runtime.remember(colorScheme, motion) {
        com.example.appblocker.ui.foundation.BloomThemeResources(
            color = BrandColors.accentHalo,
            animationSpec = motion.interactiveSpring
        )
    }
    CompositionLocalProvider(
        LocalMotion provides motion,
        LocalAuraBrush provides auraBrush,
        LocalIndication provides BloomIndication,
        com.example.appblocker.ui.foundation.LocalBloomThemeResources provides bloomResources
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
