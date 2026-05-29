package com.example.appblocker.ui.foundation

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

class Haptics internal constructor(private val context: Context) {
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private fun vibrate(effectId: Int, fallbackDuration: Long) {
        if (vibrator == null) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(effectId))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(fallbackDuration)
        }
    }

    fun lightTick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) vibrate(VibrationEffect.EFFECT_TICK, 10L)
    }

    fun mediumTick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) vibrate(VibrationEffect.EFFECT_CLICK, 20L)
    }

    fun heavyTick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) vibrate(VibrationEffect.EFFECT_HEAVY_CLICK, 30L)
    }

    fun doubleTick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) vibrate(VibrationEffect.EFFECT_DOUBLE_CLICK, 40L)
    }
    
    fun toggleOn() = heavyTick()
    fun toggleOff() = lightTick()
    fun tap() = lightTick()
    fun confirm() = mediumTick()
    fun error() = doubleTick()
}

@Composable
fun rememberHaptics(): Haptics {
    val context = LocalContext.current
    return remember(context) { Haptics(context) }
}
