package com.sharma.focusblocker.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.sharma.focusblocker.data.BlockerPreferences
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class FocusAccessibilityService : AccessibilityService() {

    private lateinit var prefs: BlockerPreferences
    private var lastForegroundPackage: String? = null

    private val checkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("FocusAccessibilityService", "Received check trigger")
            checkCurrentPackage()
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        prefs = BlockerPreferences(this)
        
        val filter = IntentFilter("com.sharma.focusblocker.ACTION_CHECK_SCHEDULE")
        androidx.core.content.ContextCompat.registerReceiver(this, checkReceiver, filter, androidx.core.content.ContextCompat.RECEIVER_NOT_EXPORTED)
        
        Log.d("FocusAccessibilityService", "Service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return
            Log.d("FocusAccessibilityService", "Foreground app changed: $packageName")
            
            if (packageName == this.packageName) {
                return
            }

            lastForegroundPackage = packageName
            checkCurrentPackage()
        }
    }
    
    private fun checkCurrentPackage() {
        val pkg = lastForegroundPackage ?: return
        val activeBlocked = ScheduleChecker.getActiveBlockedPackages(prefs.getSchedules())
        if (activeBlocked.contains(pkg)) {
            Log.d("FocusAccessibilityService", "Intercepting restricted app: $pkg")
            performGlobalAction(GLOBAL_ACTION_HOME)
        }
    }

    override fun onInterrupt() {
        // No action required
    }
    
    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(checkReceiver)
        } catch (e: Exception) {
            // Ignore
        }
    }
}
