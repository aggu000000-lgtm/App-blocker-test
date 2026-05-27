package com.sharma.focusblocker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

@Entity(tableName = "schedules")
data class Schedule(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val isEnabled: Boolean = true,
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
    val daysOfWeek: Set<Int>, // 1 = Sunday, 2 = Monday, ... 7 = Saturday
    val blockedPackages: Set<String>
) {
    fun toJson(): JSONObject {
        val json = JSONObject()
        json.put("id", id)
        json.put("name", name)
        json.put("isEnabled", isEnabled)
        json.put("startHour", startHour)
        json.put("startMinute", startMinute)
        json.put("endHour", endHour)
        json.put("endMinute", endMinute)
        
        val daysArray = JSONArray()
        daysOfWeek.forEach { daysArray.put(it) }
        json.put("daysOfWeek", daysArray)
        
        val packagesArray = JSONArray()
        blockedPackages.forEach { packagesArray.put(it) }
        json.put("blockedPackages", packagesArray)
        
        return json
    }

    companion object {
        fun fromJson(json: JSONObject): Schedule {
            val daysArray = json.getJSONArray("daysOfWeek")
            val daysOfWeek = mutableSetOf<Int>()
            for (i in 0 until daysArray.length()) {
                daysOfWeek.add(daysArray.getInt(i))
            }

            val packagesArray = json.getJSONArray("blockedPackages")
            val blockedPackages = mutableSetOf<String>()
            for (i in 0 until packagesArray.length()) {
                blockedPackages.add(packagesArray.getString(i))
            }

            return Schedule(
                id = json.getString("id"),
                name = json.getString("name"),
                isEnabled = json.getBoolean("isEnabled"),
                startHour = json.getInt("startHour"),
                startMinute = json.getInt("startMinute"),
                endHour = json.getInt("endHour"),
                endMinute = json.getInt("endMinute"),
                daysOfWeek = daysOfWeek,
                blockedPackages = blockedPackages
            )
        }
    }
}
