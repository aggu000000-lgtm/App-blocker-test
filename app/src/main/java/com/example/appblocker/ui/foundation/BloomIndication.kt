package com.example.appblocker.ui.foundation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

object BloomIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return BloomNode(interactionSource)
    }
}

private class BloomNode(
    private val interactionSource: InteractionSource
) : Modifier.Node(), DrawModifierNode, CompositionLocalConsumerModifierNode {

    private val scaleAnim = Animatable(1f)
    private val bloomAlphaAnim = Animatable(0f)
    private var pressPosition: Offset = Offset.Zero
    private var longPressJob: Job? = null
    
    // secondary visual state expansion
    private val secondaryExpansionAnim = Animatable(0f)

    override fun onAttach() {
        val context = currentValueOf(LocalContext)
        val haptics = Haptics(context)
        
        coroutineScope.launch {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> {
                        pressPosition = interaction.pressPosition
                        
                        // Haptic tap on press
                        haptics.tap()

                        launch {
                            scaleAnim.animateTo(
                                targetValue = 0.96f,
                                animationSpec = spring(stiffness = 600f, dampingRatio = 0.6f)
                            )
                        }
                        launch {
                            bloomAlphaAnim.snapTo(0f)
                            bloomAlphaAnim.animateTo(
                                targetValue = 1f,
                                animationSpec = spring(stiffness = 600f, dampingRatio = 0.6f)
                            )
                        }
                        
                        // Start long press timer
                        longPressJob = launch {
                            delay(400) // 400ms for long press
                            // secondary state visual and haptic
                            haptics.doubleTick()
                            secondaryExpansionAnim.animateTo(
                                targetValue = 1f,
                                animationSpec = spring(stiffness = 300f, dampingRatio = 0.5f)
                            )
                        }
                    }
                    is PressInteraction.Release, is PressInteraction.Cancel -> {
                        longPressJob?.cancel()
                        longPressJob = null
                        
                        launch {
                            secondaryExpansionAnim.animateTo(0f, spring(stiffness = 600f))
                        }
                        launch {
                            scaleAnim.animateTo(
                                targetValue = 1f,
                                animationSpec = spring(stiffness = 600f, dampingRatio = 0.5f) // Overshoot
                            )
                        }
                        launch {
                            bloomAlphaAnim.animateTo(
                                targetValue = 0f,
                                animationSpec = spring(stiffness = 600f, dampingRatio = 1f)
                            )
                        }
                    }
                }
            }
        }
    }

    override fun ContentDrawScope.draw() {
        // Expand drawing bounds slightly if secondary expansion is active
        val currentScale = scaleAnim.value + (0.05f * secondaryExpansionAnim.value)
        
        scale(scale = currentScale) {
            this@draw.drawContent()
            if (bloomAlphaAnim.value > 0f) {
                // Secondary state increases the radius significantly
                val expansionMultiplier = 1f + (0.5f * secondaryExpansionAnim.value)
                val radius = size.maxDimension * 0.75f * expansionMultiplier
                
                // Increase opacity on long-press
                val alpha = (0.15f * bloomAlphaAnim.value) + (0.15f * secondaryExpansionAnim.value)
                
                val brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = alpha.coerceAtMost(1f)),
                        Color.Transparent
                    ),
                    center = pressPosition,
                    radius = radius
                )
                drawRect(brush = brush)
            }
        }
    }
}
