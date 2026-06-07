package com.antigravity.distractionshield

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.antigravity.distractionshield.di.DependencyProvider

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val appContext = context.applicationContext
            // Initialize DI provider in case application class wasn't initialized yet
            DependencyProvider.initialize(appContext)
            
            val sessionRepository = DependencyProvider.sessionRepository
            if (sessionRepository.isSessionActive()) {
                val serviceIntent = Intent(appContext, AppBlockerForegroundService::class.java).apply {
                    action = AppBlockerForegroundService.ACTION_START
                }
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        appContext.startForegroundService(serviceIntent)
                    } else {
                        appContext.startService(serviceIntent)
                    }
                } catch (e: Exception) {
                    // Log or handle boot start failure silently
                }
            }
        }
    }
}
