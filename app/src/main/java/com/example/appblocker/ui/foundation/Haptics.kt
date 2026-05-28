package com.example.appblocker.ui.foundation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

/**
 * Tiny haptic vocabulary per DESIGN_NORTH_STAR.md Law 5.
 *
 * Compose's HapticFeedback API on Android only exposes two stable
 * primitives — LongPress (medium tick) and TextHandleMove (light tick).
 * We map our semantic events to those so behavior is consistent.
 */
class Haptics internal constructor(private val raw: HapticFeedback) {
    fun tap() = raw.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    fun toggleOn() = raw.performHapticFeedback(HapticFeedbackType.LongPress)
    fun toggleOff() = raw.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    fun confirm() = raw.performHapticFeedback(HapticFeedbackType.LongPress)
    fun error() {
        raw.performHapticFeedback(HapticFeedbackType.LongPress)
        raw.performHapticFeedback(HapticFeedbackType.LongPress)
    }
}

@Composable
fun rememberHaptics(): Haptics {
    val raw = LocalHapticFeedback.current
    return remember(raw) { Haptics(raw) }
}
