package com.example.designsystem.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }
val LocalAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CinematicNavHostProvider(content: @Composable () -> Unit) {
    SharedTransitionLayout {
        CompositionLocalProvider(
            LocalSharedTransitionScope provides this,
            content = content
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.cinematicSharedElement(
    key: String
): Modifier {
    val sharedScope = LocalSharedTransitionScope.current
    val animatedScope = LocalAnimatedVisibilityScope.current

    return if (sharedScope != null && animatedScope != null) {
        with(sharedScope) {
            this@cinematicSharedElement.sharedElement(
                state = rememberSharedContentState(key = key),
                animatedVisibilityScope = animatedScope,
                boundsTransform = { _, _ ->
                    tween(
                        durationMillis = CinematicDuration,
                        easing = CinematicEasing
                    )
                }
            )
        }
    } else {
        this@cinematicSharedElement
    }
}

fun Modifier.cinematicLayout(): Modifier {
    return this.animateContentSize(
        animationSpec = tween(
            durationMillis = CinematicDuration,
            easing = CinematicEasing
        )
    )
}
