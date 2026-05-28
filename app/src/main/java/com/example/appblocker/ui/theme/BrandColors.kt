package com.example.appblocker.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Brand color tokens per docs/DESIGN_NORTH_STAR.md.
 *
 * Rules:
 *  - One accent per screen, app-wide: sunset coral gradient
 *  - Backgrounds are NEAR-white / NEAR-black, never pure
 *  - Muted text is a single neutral, never a "low-emphasis primary"
 */
object BrandColors {
    // Accent — sunset coral (the only accent in the app)
    val accentStart = Color(0xFFFF6B47)
    val accentEnd = Color(0xFFFF3B5C)
    val accentGradient: Brush = Brush.linearGradient(listOf(accentStart, accentEnd))

    // Subtle accent halo for ambient mesh background
    val accentHalo = Color(0xFFFFB199)

    // Surfaces
    val lightBackground = Color(0xFFFAFAF7)
    val darkBackground = Color(0xFF0E0E10)

    val lightOnBackground = Color(0xFF111113)
    val darkOnBackground = Color(0xFFEFEFEF)

    // Muted text (single neutral; never derived from accent)
    val lightMuted = Color(0xFF6E6E73)
    val darkMuted = Color(0xFF9A9A9F)
}
