package com.example.distractionblocker.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.distractionblocker.theme.ObsidianBg
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AuroraBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "aurora_transition")

    // Animating offsets for 3 distinct liquid blobs
    val blob1Progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "blob1"
    )

    val blob2Progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "blob2"
    )

    val blob3Progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(22000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "blob3"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .blur(45.dp) // Blur the canvas elements to blend them into "liquid glass"
        ) {
            val width = size.width
            val height = size.height

            // Calculate drifting positions based on sine/cosine wave functions
            val b1X = width * 0.3f + sin(blob1Progress) * (width * 0.15f)
            val b1Y = height * 0.3f + cos(blob1Progress) * (height * 0.15f)
            val b1Radius = width * 0.45f + sin(blob1Progress * 2) * (width * 0.05f)

            val b2X = width * 0.7f + cos(blob2Progress) * (width * 0.15f)
            val b2Y = height * 0.6f + sin(blob2Progress) * (height * 0.15f)
            val b2Radius = width * 0.40f + cos(blob2Progress * 2) * (width * 0.05f)

            val b3X = width * 0.4f + sin(blob3Progress * 1.5f) * (width * 0.2f)
            val b3Y = height * 0.8f + cos(blob3Progress * 0.8f) * (height * 0.15f)
            val b3Radius = width * 0.35f + sin(blob3Progress) * (width * 0.05f)

            // Draw Neon Cyan Blob
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x3B00E5FF), Color(0x0000E5FF)),
                    center = Offset(b1X, b1Y),
                    radius = b1Radius
                ),
                center = Offset(b1X, b1Y),
                radius = b1Radius
            )

            // Draw Neon Purple Blob
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x3BD500F9), Color(0x00D500F9)),
                    center = Offset(b2X, b2Y),
                    radius = b2Radius
                ),
                center = Offset(b2X, b2Y),
                radius = b2Radius
            )

            // Draw Neon Pink Blob
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x3BFF007F), Color(0x00FF007F)),
                    center = Offset(b3X, b3Y),
                    radius = b3Radius
                ),
                center = Offset(b3X, b3Y),
                radius = b3Radius
            )
        }

        // Draw a subtle darkening/contrast overlay so text remains fully legible
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(
                    colors = listOf(Color(0x22090514), Color(0x66090514))
                ))
        )

        content()
    }
}
