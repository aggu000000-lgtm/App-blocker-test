package com.example.appblocker.ui.theme

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.IntOffset

data class Motion(
    val defaultSpring: androidx.compose.animation.core.SpringSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMediumLow
    ),
    val navigationSpring: androidx.compose.animation.core.FiniteAnimationSpec<Float> = spring(
        dampingRatio = 0.65f,
        stiffness = 1500f
    ),
    val navigationSpringIntOffset: androidx.compose.animation.core.FiniteAnimationSpec<IntOffset> = spring(
        dampingRatio = 0.65f,
        stiffness = 1500f
    ),
    val interactiveSpring: androidx.compose.animation.core.SpringSpec<Float> = spring(
        dampingRatio = 0.65f,
        stiffness = 1500f
    ),
    val interactiveSpringIntOffset: androidx.compose.animation.core.SpringSpec<IntOffset> = spring(
        dampingRatio = 0.65f,
        stiffness = 1500f
    ),
    val interactiveSpringInt: androidx.compose.animation.core.SpringSpec<Int> = spring(
        dampingRatio = 0.65f,
        stiffness = 1500f
    ),
    val interactiveSpringColor: androidx.compose.animation.core.SpringSpec<androidx.compose.ui.graphics.Color> = spring(
        dampingRatio = 0.65f,
        stiffness = 1500f
    )
)

val LocalMotion = staticCompositionLocalOf { Motion() }

val androidx.compose.material3.MaterialTheme.motion: Motion
    @Composable
    @ReadOnlyComposable
    get() = LocalMotion.current
