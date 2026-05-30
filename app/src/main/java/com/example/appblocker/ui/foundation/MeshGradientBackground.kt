package com.example.appblocker.ui.foundation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.toArgb
import com.example.appblocker.ui.theme.BrandColors
import com.example.appblocker.ui.theme.LocalMotion
import kotlin.math.cos
import kotlin.math.sin
import android.graphics.ComposeShader
import android.graphics.LinearGradient
import android.graphics.PorterDuff
import android.graphics.RadialGradient
import android.graphics.Shader

@Composable
fun rememberMeshGradientProgress(): Float {
    val isPowerSaveMode = rememberPowerSaveMode()
    val isReduceMotion = rememberIsReduceMotion()
    val isVisible = rememberIsVisible()
    val shouldAnimate = isVisible && !isReduceMotion

    val dynamism = LocalDynamism.current
    val motionTokens = LocalMotion.current

    var progress by remember { mutableFloatStateOf(0f) }

    val targetSpeed = if (isPowerSaveMode) {
        1f / DynamismLevel.Ambient.durationSeconds
    } else {
        1f / dynamism.durationSeconds
    }

    val currentSpeed by animateFloatAsState(
        targetValue = targetSpeed,
        animationSpec = androidx.compose.animation.core.tween(5000),
        label = "gradientSpeed"
    )

    LaunchedEffect(shouldAnimate) {
        if (shouldAnimate) {
            var lastTime = withFrameNanos { it }
            while (true) {
                val currentTime = withFrameNanos { it }
                val dt = (currentTime - lastTime) / 1_000_000_000f // seconds
                lastTime = currentTime
                
                progress = (progress + dt * currentSpeed) % 1f
            }
        }
    }
    
    return progress
}

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
    val t = rememberMeshGradientProgress()
    
    val isHighFidelity = LocalDynamism.current == DynamismLevel.HighFidelity
    val highFidelityAlpha by animateFloatAsState(
        targetValue = if (isHighFidelity) 1f else 0f,
        animationSpec = androidx.compose.animation.core.tween(5000),
        label = "highFidelityAlpha"
    )

    this
        .fillMaxSize()
        .drawBehind {
            val w = size.width
            val h = size.height
            val twoPi = (2f * Math.PI).toFloat()

            // halo 1 — top-left-ish, drifting slowly
            val c1 = Offset(
                x = w * (0.25f + 0.10f * cos(t * twoPi)),
                y = h * (0.20f + 0.08f * sin(t * twoPi))
            )
            val r1 = maxOf(w, h) * 0.65f

            // halo 2 — bottom-right-ish, drifting in counterphase
            val c2 = Offset(
                x = w * (0.80f + 0.10f * cos(t * twoPi + Math.PI.toFloat())),
                y = h * (0.80f + 0.08f * sin(t * twoPi + Math.PI.toFloat()))
            )
            val r2 = maxOf(w, h) * 0.55f
            
            // halo 3 - spectral blue
            val c3 = Offset(
                x = w * (0.50f + 0.15f * cos(t * twoPi * 1.5f)),
                y = h * (0.50f + 0.15f * sin(t * twoPi * 1.5f))
            )
            val r3 = maxOf(w, h) * 0.50f

            // halo 4 - spectral purple
            val c4 = Offset(
                x = w * (0.20f + 0.20f * cos(t * twoPi * 0.8f + Math.PI.toFloat())),
                y = h * (0.80f + 0.10f * sin(t * twoPi * 0.8f + Math.PI.toFloat()))
            )
            val r4 = maxOf(w, h) * 0.60f

            val gradient1 = RadialGradient(
                c1.x, c1.y, r1,
                intArrayOf(BrandColors.accentHalo.copy(alpha = 0.18f).toArgb(), android.graphics.Color.TRANSPARENT),
                null,
                Shader.TileMode.CLAMP
            )
            
            val gradient2 = RadialGradient(
                c2.x, c2.y, r2,
                intArrayOf(BrandColors.accentEnd.copy(alpha = 0.12f).toArgb(), android.graphics.Color.TRANSPARENT),
                null,
                Shader.TileMode.CLAMP
            )
            
            val gradient3 = RadialGradient(
                c3.x, c3.y, r3,
                intArrayOf(Color(0xFF56CCF2).copy(alpha = 0.15f * highFidelityAlpha).toArgb(), android.graphics.Color.TRANSPARENT),
                null,
                Shader.TileMode.CLAMP
            )
            
            val gradient4 = RadialGradient(
                c4.x, c4.y, r4,
                intArrayOf(Color(0xFF9B51E0).copy(alpha = 0.15f * highFidelityAlpha).toArgb(), android.graphics.Color.TRANSPARENT),
                null,
                Shader.TileMode.CLAMP
            )
            
            val baseShader = LinearGradient(
                0f, 0f, 0f, 10f,
                intArrayOf(base.toArgb(), base.toArgb()),
                null,
                Shader.TileMode.CLAMP
            )

            // Combine halos using ADD blending
            val halos12 = ComposeShader(
                gradient2, gradient1, PorterDuff.Mode.ADD
            )
            val halos34 = ComposeShader(
                gradient4, gradient3, PorterDuff.Mode.ADD
            )
            val combinedHalos = ComposeShader(
                halos12, halos34, PorterDuff.Mode.ADD
            )
            
            // Draw halos over the base color
            val finalShader = ComposeShader(
                baseShader, combinedHalos, PorterDuff.Mode.SRC_OVER
            )

            drawRect(
                brush = ShaderBrush(finalShader)
            )
        }
}
