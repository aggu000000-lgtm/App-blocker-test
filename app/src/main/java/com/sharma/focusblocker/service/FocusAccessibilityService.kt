package com.sharma.focusblocker.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.sharma.focusblocker.data.BlockerPreferences

class FocusAccessibilityService : AccessibilityService() {

    private lateinit var prefs: BlockerPreferences

    override fun onServiceConnected() {
        super.onServiceConnected()
        prefs = BlockerPreferences(this)
        Log.d("FocusAccessibilityService", "Service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return
            Log.d("FocusAccessibilityService", "Foreground app changed: $packageName")
            
            // Check if our own app is in the foreground? We don't block ourselves.
            if (packageName == this.packageName) {
                return
            }

            val restrictedPackages = prefs.getRestrictedPackages()
            if (restrictedPackages.contains(packageName)) {
                Log.d("FocusAccessibilityService", "Intercepting restricted app: $packageName")
                // Intercept and return to home screen
                performGlobalAction(GLOBAL_ACTION_HOME)
            }
        }
    }

    override fun onInterrupt() {
        // No action required
    }
}
