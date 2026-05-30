package com.example.appblocker.ui.foundation

import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private val FallbackTopLeft = Offset(-1f, -1f)
private val FallbackSize = Size(2f, 2f)

class BloomThemeResources(
    val color: Color,
    val animationSpec: androidx.compose.animation.core.SpringSpec<Float>
) {
    val runtimeShader: android.graphics.RuntimeShader? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.graphics.RuntimeShader("""
                uniform float2 resolution;
                uniform float2 center;
                uniform float radius;
                uniform float alpha;
                uniform half4 color;

                half4 main(float2 fragCoord) {
                    float d = distance(fragCoord, center);
                    float t = clamp(1.0 - (d / radius), 0.0, 1.0);
                    float falloff = t * t * (3.0 - 2.0 * t);
                    return color * falloff * alpha;
                }
            """.trimIndent())
        } else {
            null
        }
    }

    val shaderBrush: androidx.compose.ui.graphics.ShaderBrush? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            runtimeShader?.let { androidx.compose.ui.graphics.ShaderBrush(it) }
        } else {
            null
        }
    }

    val fallbackBrush: Brush by lazy {
        Brush.radialGradient(
            colors = listOf(color, Color.Transparent),
            center = Offset.Zero,
            radius = 1f
        )
    }
}

val LocalBloomThemeResources = androidx.compose.runtime.staticCompositionLocalOf<BloomThemeResources> {
    error("No BloomThemeResources provided")
}

object BloomIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return BloomNode(interactionSource)
    }

    override fun equals(other: Any?): Boolean = other === this
    override fun hashCode(): Int = -1
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
        val bloomResources = currentValueOf(LocalBloomThemeResources)
        
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
                                animationSpec = bloomResources.animationSpec
                            )
                        }
                        launch {
                            bloomAlphaAnim.snapTo(0f)
                            bloomAlphaAnim.animateTo(
                                targetValue = 1f,
                                animationSpec = bloomResources.animationSpec
                            )
                        }
                        
                        // Start long press timer
                        longPressJob = launch {
                            delay(400) // 400ms for long press
                            // secondary state visual and haptic
                            haptics.doubleTick()
                            secondaryExpansionAnim.animateTo(
                                targetValue = 1f,
                                animationSpec = bloomResources.animationSpec
                            )
                        }
                    }
                    is PressInteraction.Release, is PressInteraction.Cancel -> {
                        longPressJob?.cancel()
                        longPressJob = null
                        
                        launch {
                            secondaryExpansionAnim.animateTo(0f, bloomResources.animationSpec)
                        }
                        launch {
                            scaleAnim.animateTo(
                                targetValue = 1f,
                                animationSpec = bloomResources.animationSpec
                            )
                        }
                        launch {
                            bloomAlphaAnim.animateTo(
                                targetValue = 0f,
                                animationSpec = bloomResources.animationSpec
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
                val bloomResources = currentValueOf(LocalBloomThemeResources)
                
                // Secondary state increases the radius significantly
                val expansionMultiplier = 1f + (0.5f * secondaryExpansionAnim.value)
                val radius = size.maxDimension * 0.75f * expansionMultiplier
                
                // Increase opacity on long-press
                val alpha = ((0.15f * bloomAlphaAnim.value) + (0.15f * secondaryExpansionAnim.value)).coerceAtMost(1f)
                
                val shader = bloomResources.runtimeShader
                val shaderBrush = bloomResources.shaderBrush
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && shader != null && shaderBrush != null) {
                    shader.setFloatUniform("resolution", size.width, size.height)
                    shader.setFloatUniform("center", pressPosition.x, pressPosition.y)
                    shader.setFloatUniform("radius", radius)
                    shader.setFloatUniform("alpha", alpha)
                    shader.setColorUniform("color", bloomResources.color.toArgb())
                    drawRect(brush = shaderBrush)
                } else {
                    withTransform({
                        translate(pressPosition.x, pressPosition.y)
                        scale(radius, radius)
                    }) {
                        drawRect(
                            brush = bloomResources.fallbackBrush,
                            topLeft = FallbackTopLeft,
                            size = FallbackSize,
                            alpha = alpha
                        )
                    }
                }
            }
        }
    }
}
