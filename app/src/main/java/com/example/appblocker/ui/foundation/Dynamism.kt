package com.example.appblocker.ui.foundation

import androidx.compose.runtime.staticCompositionLocalOf

enum class DynamismLevel(val durationSeconds: Float) {
    Ambient(16f),
    Active(5f),
    Intense(2f) // Maybe intense isn't needed or is 2s, but problem says between 16s and 5s cycles.
}

val LocalDynamism = staticCompositionLocalOf { DynamismLevel.Ambient }
