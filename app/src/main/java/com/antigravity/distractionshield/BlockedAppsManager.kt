package com.antigravity.distractionshield

import android.content.Context
import android.content.SharedPreferences

class BlockedAppsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "distraction_blocker_prefs"
        private const val KEY_BLOCKED_PACKAGES = "blocked_packages"
        private const val KEY_SESSION_END_TIME = "session_end_time"
        private const val KEY_SESSION_TOTAL_DURATION = "session_total_duration"
    }

    /**
     * Checks if a focus blocking session is currently active.
     */
    fun isSessionActive(): Boolean {
        val endTime = getSessionEndTime()
        return endTime > 0 && System.currentTimeMillis() < endTime
    }

    /**
     * Starts a new deep focus block session.
     */
    fun startSession(durationMillis: Long) {
        val endTime = System.currentTimeMillis() + durationMillis
        prefs.edit()
            .putLong(KEY_SESSION_END_TIME, endTime)
            .putLong(KEY_SESSION_TOTAL_DURATION, durationMillis)
            .apply()
    }

    /**
     * Extends the current active session by the specified duration.
     */
    fun extendSession(durationMillis: Long) {
        val currentEndTime = getSessionEndTime()
        val baseTime = if (currentEndTime > System.currentTimeMillis()) currentEndTime else System.currentTimeMillis()
        val newEndTime = baseTime + durationMillis
        val totalDuration = newEndTime - (currentEndTime - getSessionTotalDuration())

        prefs.edit()
            .putLong(KEY_SESSION_END_TIME, newEndTime)
            .putLong(KEY_SESSION_TOTAL_DURATION, totalDuration)
            .apply()
    }

    /**
     * Forces the current focus block session to end.
     */
    fun endSession() {
        prefs.edit()
            .putLong(KEY_SESSION_END_TIME, 0L)
            .putLong(KEY_SESSION_TOTAL_DURATION, 0L)
            .apply()
    }

    /**
     * Returns the epoch millisecond when the current session ends.
     */
    fun getSessionEndTime(): Long {
        return prefs.getLong(KEY_SESSION_END_TIME, 0L)
    }

    /**
     * Returns the total duration of the current session.
     */
    fun getSessionTotalDuration(): Long {
        return prefs.getLong(KEY_SESSION_TOTAL_DURATION, 0L)
    }

    /**
     * Retrieves the set of all blocked package names.
     */
    fun getBlockedApps(): Set<String> {
        return prefs.getStringSet(KEY_BLOCKED_PACKAGES, emptySet()) ?: emptySet()
    }

    /**
     * Adds a package name to the blocklist.
     */
    fun addBlockedApp(packageName: String) {
        val current = getBlockedApps().toMutableSet()
        if (current.add(packageName)) {
            prefs.edit().putStringSet(KEY_BLOCKED_PACKAGES, current).apply()
        }
    }

    /**
     * Removes a package name from the blocklist.
     */
    fun removeBlockedApp(packageName: String) {
        val current = getBlockedApps().toMutableSet()
        if (current.remove(packageName)) {
            prefs.edit().putStringSet(KEY_BLOCKED_PACKAGES, current).apply()
        }
    }
}
