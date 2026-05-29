package com.example.appblocker.ui.foundation

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback

class Haptics internal constructor(
    private val context: Context,
    private val raw: HapticFeedback
) {
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        manager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    private fun performAdvancedHaptic(effectId: Int, fallback: HapticFeedbackType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && vibrator?.hasVibrator() == true) {
            vibrator.vibrate(VibrationEffect.createPredefined(effectId))
        } else {
            raw.performHapticFeedback(fallback)
        }
    }

    fun light() = performAdvancedHaptic(VibrationEffect.EFFECT_TICK, HapticFeedbackType.TextHandleMove)
    fun medium() = performAdvancedHaptic(VibrationEffect.EFFECT_CLICK, HapticFeedbackType.LongPress)
    fun heavy() = performAdvancedHaptic(VibrationEffect.EFFECT_HEAVY_CLICK, HapticFeedbackType.LongPress)
    fun doubleTick() = performAdvancedHaptic(VibrationEffect.EFFECT_DOUBLE_CLICK, HapticFeedbackType.LongPress)

    fun tap() = light()
    fun toggleOn() = heavy()
    fun toggleOff() = light()
    fun confirm() = medium()
    fun longPress() = doubleTick()
    fun error() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && vibrator?.hasVibrator() == true) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))
        } else {
            raw.performHapticFeedback(HapticFeedbackType.LongPress)
            raw.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }
}

@Composable
fun rememberHaptics(): Haptics {
    val context = LocalContext.current
    val raw = LocalHapticFeedback.current
    return remember(context, raw) { Haptics(context, raw) }
}
