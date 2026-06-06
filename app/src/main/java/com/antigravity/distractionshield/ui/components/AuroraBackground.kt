package com.antigravity.distractionshield.ui.components

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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import com.antigravity.distractionshield.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AuroraBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "aurora_transition")

    // Dynamic morphing speed parameter
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "morph_progress"
    )

    // Center offset animation for drifting blobs
    val driftX by infiniteTransition.animateFloat(
        initialValue = -50f,
        targetValue = 50f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = SineClampedEasing()),
            repeatMode = RepeatMode.Reverse
        ),
        label = "drift_x"
    )

    val driftY by infiniteTransition.animateFloat(
        initialValue = -80f,
        targetValue = 80f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = SineClampedEasing()),
            repeatMode = RepeatMode.Reverse
        ),
        label = "drift_y"
    )

    val baseBgColor = MaterialTheme.colorScheme.background
    val blobColors = LocalThemeConfig.current.auroraBlobColors
    val colors1 = if (blobColors.size >= 5) blobColors else listOf(
        GradientGreen.copy(alpha = 0.55f),
        GradientYellow.copy(alpha = 0.6f),
        GradientRed.copy(alpha = 0.55f),
        GradientPink.copy(alpha = 0.6f),
        GradientMagenta.copy(alpha = 0.55f)
    )
    val colors2 = if (blobColors.size >= 5) {
        listOf(blobColors[4], blobColors[3], blobColors[2], blobColors[1], blobColors[0])
    } else {
        listOf(
            GradientMagenta.copy(alpha = 0.4f),
            GradientPink.copy(alpha = 0.45f),
            GradientRed.copy(alpha = 0.4f),
            GradientYellow.copy(alpha = 0.45f),
            GradientGreen.copy(alpha = 0.4f)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(baseBgColor)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .blur(65.dp) // High radius blur to diffuse the intense fluid colors
        ) {
            val w = size.width
            val h = size.height

            // 1. Primary Blob: Irregular, morphing shape with 5 colors
            val path1 = Path()
            val cx1 = w * 0.5f + driftX
            val cy1 = h * 0.45f + driftY
            val baseRadius1 = w * 0.45f
            val numPoints = 8
            val points1 = ArrayList<Offset>()

            for (i in 0 until numPoints) {
                val angle = i * (2f * Math.PI.toFloat() / numPoints)
                // Modulate radius by multiple sine/cosine frequencies to create irregular shape
                val rOffset = sin(progress + i * 1.3f) * (w * 0.12f) + cos(progress * 0.8f - i * 2.1f) * (w * 0.08f)
                val r = (baseRadius1 + rOffset).coerceAtLeast(50f)
                val px = cx1 + r * cos(angle)
                val py = cy1 + r * sin(angle)
                points1.add(Offset(px, py))
            }

            // Draw smooth bezier path
            path1.moveTo(points1[0].x, points1[0].y)
            for (i in 0 until numPoints) {
                val current = points1[i]
                val next = points1[(i + 1) % numPoints]
                val prev = points1[(i - 1 + numPoints) % numPoints]
                val nextNext = points1[(i + 2) % numPoints]

                val cp1X = current.x + (next.x - prev.x) / 5f
                val cp1Y = current.y + (next.y - prev.y) / 5f
                val cp2X = next.x - (nextNext.x - current.x) / 5f
                val cp2Y = next.y - (nextNext.y - current.y) / 5f

                path1.cubicTo(cp1X, cp1Y, cp2X, cp2Y, next.x, next.y)
            }
            path1.close()

            // 5-Color Linear Gradient: Green, Yellow, Red, Pink, Magenta
            val brush5Color = Brush.linearGradient(
                colors = colors1,
                start = Offset(cx1 - baseRadius1, cy1 - baseRadius1),
                end = Offset(cx1 + baseRadius1, cy1 + baseRadius1)
            )
            drawPath(path = path1, brush = brush5Color)

            // 2. Secondary Companion Blob (Slightly smaller, opposite drift, shifting gradient)
            val path2 = Path()
            val cx2 = w * 0.55f - driftX
            val cy2 = h * 0.55f - driftY
            val baseRadius2 = w * 0.35f
            val points2 = ArrayList<Offset>()

            for (i in 0 until numPoints) {
                val angle = i * (2f * Math.PI.toFloat() / numPoints)
                val rOffset = cos(progress * 1.1f + i * 1.5f) * (w * 0.09f) + sin(progress * 0.6f - i * 1.1f) * (w * 0.07f)
                val r = (baseRadius2 + rOffset).coerceAtLeast(50f)
                val px = cx2 + r * cos(angle)
                val py = cy2 + r * sin(angle)
                points2.add(Offset(px, py))
            }

            path2.moveTo(points2[0].x, points2[0].y)
            for (i in 0 until numPoints) {
                val current = points2[i]
                val next = points2[(i + 1) % numPoints]
                val prev = points2[(i - 1 + numPoints) % numPoints]
                val nextNext = points2[(i + 2) % numPoints]

                val cp1X = current.x + (next.x - prev.x) / 5f
                val cp1Y = current.y + (next.y - prev.y) / 5f
                val cp2X = next.x - (nextNext.x - current.x) / 5f
                val cp2Y = next.y - (nextNext.y - current.y) / 5f

                path2.cubicTo(cp1X, cp1Y, cp2X, cp2Y, next.x, next.y)
            }
            path2.close()

            val brushCompanion = Brush.linearGradient(
                colors = colors2,
                start = Offset(cx2 + baseRadius2, cy2 - baseRadius2),
                end = Offset(cx2 - baseRadius2, cy2 + baseRadius2)
            )
            drawPath(path = path2, brush = brushCompanion)
        }

        // Draw a light translucent tint over everything to unify colors and ensure text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(
                    colors = listOf(baseBgColor.copy(alpha = 0.0f), baseBgColor.copy(alpha = 0.25f))
                ))
        )

        content()
    }
}

/**
 * A custom easing representing a smooth sine curve for natural reversal in animations
 */
private class SineClampedEasing : Easing {
    override fun transform(fraction: Float): Float {
        return (sin((fraction - 0.5f) * Math.PI.toFloat()) + 1f) / 2f
    }
}

