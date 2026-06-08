package com.antigravity.distractionshield.data.datasource

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate

class PreferencesDataSource(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "distraction_blocker_prefs"
        const val KEY_THEME_MODE = "theme_mode"
        const val KEY_USE_DYNAMIC_COLOR = "use_dynamic_color"
        const val KEY_SESSION_END_TIME = "session_end_time"
        const val KEY_SESSION_TOTAL_DURATION = "session_total_duration"
        const val KEY_BLOCKED_PACKAGES = "blocked_packages"
    }

    fun getThemeMode(): String = prefs.getString(KEY_THEME_MODE, "SYSTEM") ?: "SYSTEM"
    fun setThemeMode(mode: String) = prefs.edit().putString(KEY_THEME_MODE, mode).apply()

    fun getUseDynamicColor(): Boolean = prefs.getBoolean(KEY_USE_DYNAMIC_COLOR, false)
    fun setUseDynamicColor(use: Boolean) = prefs.edit().putBoolean(KEY_USE_DYNAMIC_COLOR, use).apply()

    fun getSessionEndTime(): Long = prefs.getLong(KEY_SESSION_END_TIME, 0L)
    fun getSessionTotalDuration(): Long = prefs.getLong(KEY_SESSION_TOTAL_DURATION, 0L)

    fun startSession(durationMillis: Long) {
        val endTime = System.currentTimeMillis() + durationMillis
        prefs.edit()
            .putLong(KEY_SESSION_END_TIME, endTime)
            .putLong(KEY_SESSION_TOTAL_DURATION, durationMillis)
            .apply()
    }

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

    fun endSession() {
        prefs.edit()
            .putLong(KEY_SESSION_END_TIME, 0L)
            .putLong(KEY_SESSION_TOTAL_DURATION, 0L)
            .apply()
    }

    fun getBlockedApps(): Set<String> = prefs.getStringSet(KEY_BLOCKED_PACKAGES, emptySet()) ?: emptySet()

    fun addBlockedApp(packageName: String) {
        val current = getBlockedApps().toMutableSet()
        if (current.add(packageName)) {
            prefs.edit().putStringSet(KEY_BLOCKED_PACKAGES, current).apply()
        }
    }

    fun removeBlockedApp(packageName: String) {
        val current = getBlockedApps().toMutableSet()
        if (current.remove(packageName)) {
            prefs.edit().putStringSet(KEY_BLOCKED_PACKAGES, current).apply()
        }
    }

    fun getDailyFocusDuration(dateStr: String): Long = prefs.getLong("stats_focus_duration_$dateStr", 0L)
    fun incrementDailyFocusDuration(dateStr: String, ms: Long) {
        val current = getDailyFocusDuration(dateStr)
        prefs.edit().putLong("stats_focus_duration_$dateStr", current + ms).apply()
    }

    fun getDailyBlockedAttempts(dateStr: String): Int = prefs.getInt("stats_blocked_attempts_$dateStr", 0)
    fun incrementDailyBlockedAttempts(dateStr: String) {
        val current = getDailyBlockedAttempts(dateStr)
        prefs.edit().putInt("stats_blocked_attempts_$dateStr", current + 1).apply()
    }

    fun getDailyRapidSwitches(dateStr: String): Int = prefs.getInt("stats_rapid_switches_$dateStr", 0)
    fun incrementDailyRapidSwitches(dateStr: String) {
        val current = getDailyRapidSwitches(dateStr)
        prefs.edit().putInt("stats_rapid_switches_$dateStr", current + 1).apply()
    }

    val preferenceChangesFlow: Flow<String> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key != null) {
                trySend(key)
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }.conflate()
}
