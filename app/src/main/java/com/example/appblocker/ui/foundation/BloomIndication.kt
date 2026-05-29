package com.example.appblocker.ui.foundation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BloomIndication(
    private val color: Color,
    private val pressedScale: Float = 0.96f
) : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return BloomNode(interactionSource, color, pressedScale)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as BloomIndication
        if (color != other.color) return false
        if (pressedScale != other.pressedScale) return false
        return true
    }

    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + pressedScale.hashCode()
        return result
    }
}

private class BloomNode(
    private val interactionSource: InteractionSource,
    private val color: Color,
    private val pressedScale: Float
) : androidx.compose.ui.Modifier.Node(), DrawModifierNode {

    private val scaleAnim = Animatable(1f)
    private val bloomAnim = Animatable(0f)
    private var pressOffset = Offset.Zero

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collectLatest { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> {
                        pressOffset = interaction.pressPosition
                        launch {
                            scaleAnim.animateTo(
                                targetValue = pressedScale,
                                animationSpec = spring(stiffness = 600f, dampingRatio = 0.7f)
                            )
                        }
                        launch {
                            bloomAnim.snapTo(0f)
                            bloomAnim.animateTo(
                                targetValue = 1f,
                                animationSpec = spring(stiffness = 300f, dampingRatio = 0.8f)
                            )
                        }
                    }
                    is PressInteraction.Release, is PressInteraction.Cancel -> {
                        launch {
                            scaleAnim.animateTo(
                                targetValue = 1f,
                                animationSpec = spring(stiffness = 500f, dampingRatio = 0.6f)
                            )
                        }
                        launch {
                            bloomAnim.animateTo(
                                targetValue = 0f,
                                animationSpec = spring(stiffness = 300f, dampingRatio = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }

    override fun ContentDrawScope.draw() {
        scale(scaleAnim.value) {
            this@draw.drawContent()
            if (bloomAnim.value > 0f) {
                val radius = size.maxDimension * 1.5f * bloomAnim.value
                val center = if (pressOffset != Offset.Zero) pressOffset else center
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(color.copy(alpha = 0.2f), Color.Transparent),
                        center = center,
                        radius = radius.coerceAtLeast(1f)
                    ),
                    radius = radius,
                    center = center
                )
            }
        }
    }
}
