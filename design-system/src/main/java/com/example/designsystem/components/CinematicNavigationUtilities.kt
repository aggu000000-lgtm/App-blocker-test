package com.example.designsystem.components

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay

val CinematicEasing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
val CinematicDuration = 500

fun cinematicEnterTransition() = fadeIn(
    animationSpec = tween(CinematicDuration, easing = CinematicEasing)
) + scaleIn(
    initialScale = 0.95f,
    animationSpec = tween(CinematicDuration, easing = CinematicEasing)
)

fun cinematicExitTransition() = fadeOut(
    animationSpec = tween(CinematicDuration, easing = CinematicEasing)
) + scaleOut(
    targetScale = 1.05f,
    animationSpec = tween(CinematicDuration, easing = CinematicEasing)
)

fun cinematicPopEnterTransition() = fadeIn(
    animationSpec = tween(CinematicDuration, easing = CinematicEasing)
) + scaleIn(
    initialScale = 1.05f,
    animationSpec = tween(CinematicDuration, easing = CinematicEasing)
)

fun cinematicPopExitTransition() = fadeOut(
    animationSpec = tween(CinematicDuration, easing = CinematicEasing)
) + scaleOut(
    targetScale = 0.95f,
    animationSpec = tween(CinematicDuration, easing = CinematicEasing)
)

@Composable
fun CinematicHapticFeedback() {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        delay(CinematicDuration / 2L)
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}
