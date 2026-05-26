package com.sharma.focusblocker.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootAndAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED || action == "com.sharma.focusblocker.ACTION_CHECK_SCHEDULE") {
            // Re-calculate and set the next alarm
            ScheduleAlarmManager.updateAlarms(context)
        }
    }
}
