package com.antigravity.distractionshield.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.antigravity.distractionshield.theme.GlassBase
import com.antigravity.distractionshield.theme.GlassBorderDark
import com.antigravity.distractionshield.theme.GlassBorderLight

@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 18.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val glassShape = RoundedCornerShape(cornerRadius)

    // Infinite transition for the diagonal specular sweep (reflection shine)
    val infiniteTransition = rememberInfiniteTransition(label = "glass_shine_transition")
    val shineProgress by infiniteTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shine_progress"
    )

    Box(
        modifier = modifier
            // 1. Soft 3D Drop Shadow (lifts card off white background)
            .shadow(
                elevation = 10.dp,
                shape = glassShape,
                clip = false,
                ambientColor = Color.Black.copy(alpha = 0.05f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .clip(glassShape)
            // 2. High-translucency glass base
            .background(GlassBase)
            // 3. 3D Refraction Border (diagonal gradient: bright top-left to fading dark bottom-right)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(GlassBorderLight, GlassBorderDark),
                    start = Offset(0f, 0f),
                    end = Offset.Infinite
                ),
                shape = glassShape
            )
    ) {
        // 4. Polished specular light sweep overlay (animated reflection)
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            val startX = w * shineProgress
            val endX = startX + w * 0.35f

            val shineBrush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.0f),
                    Color.White.copy(alpha = 0.18f), // polished sheen reflection
                    Color.White.copy(alpha = 0.0f)
                ),
                start = Offset(startX, 0f),
                end = Offset(endX, h)
            )

            drawRoundRect(
                brush = shineBrush,
                cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
            )

            // 5. Specular Inner Rim highlight (subtle 3D glow on inner borders)
            drawRoundRect(
                color = Color.White.copy(alpha = 0.15f),
                style = Stroke(width = 0.8.dp.toPx()),
                cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
            )
        }

        // Card Content
        content()
    }
}

