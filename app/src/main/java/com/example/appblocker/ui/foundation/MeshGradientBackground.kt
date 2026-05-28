package com.example.appblocker.ui.foundation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.appblocker.ui.theme.BrandColors
import kotlin.math.cos
import kotlin.math.sin

/**
 * Slow, ambient mesh-gradient background.
 *
 * Per DESIGN_NORTH_STAR.md Law 2: gradients are reactive / alive,
 * never decorative wallpaper. This one drifts over ~16 seconds so it
 * is *barely* perceptible — you feel the screen breathe, you don't
 * see colors moving.
 *
 * Implementation: two radial halos of the accent color at opposing
 * corners, each orbiting on its own slow timer. Alpha is intentionally
 * tiny so the background never competes with content.
 */
fun Modifier.meshGradientBackground(base: Color): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "mesh")
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 16_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "meshT"
    )

    this
        .fillMaxSize()
        .drawBehind {
            // base fill
            drawRect(base)

            val w = size.width
            val h = size.height
            val twoPi = (2f * Math.PI).toFloat()

            // halo 1 — top-left-ish, drifting slowly
            val c1 = Offset(
                x = w * (0.25f + 0.10f * cos(t * twoPi)),
                y = h * (0.20f + 0.08f * sin(t * twoPi))
            )
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        BrandColors.accentHalo.copy(alpha = 0.18f),
                        Color.Transparent
                    ),
                    center = c1,
                    radius = maxOf(w, h) * 0.65f
                )
            )

            // halo 2 — bottom-right-ish, drifting in counterphase
            val c2 = Offset(
                x = w * (0.80f + 0.10f * cos(t * twoPi + Math.PI.toFloat())),
                y = h * (0.80f + 0.08f * sin(t * twoPi + Math.PI.toFloat()))
            )
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        BrandColors.accentEnd.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = c2,
                    radius = maxOf(w, h) * 0.55f
                )
            )
        }
}
