package com.example.appblocker.ui.foundation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf

enum class DynamismLevel(val durationSeconds: Float) {
    Ambient(16f),
    Active(5f),
    HighFidelity(2.5f)
}

val LocalDynamism = staticCompositionLocalOf { DynamismLevel.Ambient }
val LocalAdaptiveVisuals = compositionLocalOf<MutableState<Boolean>> { error("No adaptive visuals provided") }
