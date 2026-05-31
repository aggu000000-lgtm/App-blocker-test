package com.example.appblocker.ui.foundation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat

object BroadcastUtil {
    fun registerReceiver(context: Context, receiver: BroadcastReceiver, filter: IntentFilter): Intent? {
        return ContextCompat.registerReceiver(
            context,
            receiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }
}
