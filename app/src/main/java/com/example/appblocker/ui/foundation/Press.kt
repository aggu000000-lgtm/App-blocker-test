package com.example.appblocker.ui.foundation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Spring-physics press feedback per DESIGN_NORTH_STAR.md Law 1.
 *
 * - Press : scales DOWN to 0.96 with a stiff spring
 * - Release: spring-back with overshoot (medium-low damping)
 *
 * Combine with [Modifier.onPressOffset] if you also need the touch
 * coordinate for gradient-bloom effects.
 */
fun Modifier.springPress(
    interactionSource: MutableInteractionSource,
    pressedScale: Float = 0.96f
): Modifier = composed {
    val isPressed by interactionSource.collectIsPressedAsStateCompat()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1f,
        animationSpec = if (isPressed) {
            spring(stiffness = Spring.StiffnessMedium)
        } else {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        },
        label = "springPress"
    )
    this.scale(scale)
}

/**
 * Captures the latest down-press coordinate (in local pixel space).
 * Used to drive a gradient bloom radiating from where the finger touched.
 */
fun Modifier.onPressOffset(onOffset: (Offset) -> Unit): Modifier = composed {
    pointerInput(Unit) {
        detectTapGestures(onPress = { offset ->
            onOffset(offset)
        })
    }
}

/** Compose's collectIsPressedAsState (re-implemented to avoid an extra import). */
@Composable
private fun MutableInteractionSource.collectIsPressedAsStateCompat(): androidx.compose.runtime.State<Boolean> {
    val state = remember { mutableStateOf(false) }
    LaunchedEffect(this) {
        val pressed = mutableListOf<PressInteraction.Press>()
        interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> pressed.add(interaction)
                is PressInteraction.Release -> pressed.remove(interaction.press)
                is PressInteraction.Cancel -> pressed.remove(interaction.press)
            }
            state.value = pressed.isNotEmpty()
        }
    }
    return state
}
