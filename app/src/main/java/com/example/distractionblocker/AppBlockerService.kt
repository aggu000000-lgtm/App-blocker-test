package com.example.distractionblocker

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.util.Log

class AppBlockerService : AccessibilityService() {

    private lateinit var blockedAppsManager: BlockedAppsManager

    override fun onCreate() {
        super.onCreate()
        blockedAppsManager = BlockedAppsManager(applicationContext)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        // We listen to window state changes (e.g. app opening, dialog opening)
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return
            
            // Avoid blocking our own app!
            if (packageName == this.packageName) return

            // Check if the current app is blocked and a focus session is active
            if (blockedAppsManager.isSessionActive()) {
                val blockedApps = blockedAppsManager.getBlockedApps()
                if (blockedApps.contains(packageName)) {
                    Log.d("AppBlockerService", "Intercepted: $packageName. Redirecting to BlockerActivity.")
                    
                    // Launch the full-screen blocker overlay activity
                    val intent = Intent(this, BlockerActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        // Pass the name of the package being blocked for visual reference
                        putExtra(BlockerActivity.EXTRA_BLOCKED_PACKAGE, packageName)
                    }
                    startActivity(intent)
                }
            }
        }
    }

    override fun onInterrupt() {
        Log.d("AppBlockerService", "Service Interrupted")
    }
}
