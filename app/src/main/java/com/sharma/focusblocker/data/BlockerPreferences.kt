package com.sharma.focusblocker.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

class BlockerPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("blocker_prefs", Context.MODE_PRIVATE)

    init {
        migrateIfNeeded()
    }

    private fun migrateIfNeeded() {
        if (!prefs.contains("schedules") && prefs.contains("restricted_packages")) {
            val oldPackages = prefs.getStringSet("restricted_packages", setOf()) ?: setOf()
            if (oldPackages.isNotEmpty()) {
                val legacySchedule = Schedule(
                    name = "Legacy Always On",
                    isEnabled = true,
                    startHour = 0,
                    startMinute = 0,
                    endHour = 23,
                    endMinute = 59,
                    daysOfWeek = setOf(1, 2, 3, 4, 5, 6, 7),
                    blockedPackages = oldPackages
                )
                saveSchedules(listOf(legacySchedule))
            }
            prefs.edit().remove("restricted_packages").apply()
        }
    }

    fun getSchedules(): List<Schedule> {
        val jsonString = prefs.getString("schedules", null) ?: return emptyList()
        val array = JSONArray(jsonString)
        val list = mutableListOf<Schedule>()
        for (i in 0 until array.length()) {
            list.add(Schedule.fromJson(array.getJSONObject(i)))
        }
        return list
    }

    fun saveSchedules(schedules: List<Schedule>) {
        val array = JSONArray()
        schedules.forEach { array.put(it.toJson()) }
        prefs.edit().putString("schedules", array.toString()).apply()
    }

    fun hasMigratedToRoom(): Boolean {
        return prefs.getBoolean("migrated_to_room", false)
    }

    fun setMigratedToRoom(migrated: Boolean) {
        prefs.edit().putBoolean("migrated_to_room", migrated).apply()
    }
}
