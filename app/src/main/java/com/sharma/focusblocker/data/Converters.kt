package com.sharma.focusblocker.data

import androidx.room.TypeConverter
import org.json.JSONArray

class Converters {
    @TypeConverter
    fun fromIntSet(value: Set<Int>): String {
        val array = JSONArray()
        value.forEach { array.put(it) }
        return array.toString()
    }

    @TypeConverter
    fun toIntSet(value: String): Set<Int> {
        val set = mutableSetOf<Int>()
        val array = JSONArray(value)
        for (i in 0 until array.length()) {
            set.add(array.getInt(i))
        }
        return set
    }

    @TypeConverter
    fun fromStringSet(value: Set<String>): String {
        val array = JSONArray()
        value.forEach { array.put(it) }
        return array.toString()
    }

    @TypeConverter
    fun toStringSet(value: String): Set<String> {
        val set = mutableSetOf<String>()
        val array = JSONArray(value)
        for (i in 0 until array.length()) {
            set.add(array.getString(i))
        }
        return set
    }
}
