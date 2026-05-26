package com.sharma.focusblocker.data

import android.content.Context
import android.content.SharedPreferences

class BlockerPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("blocker_prefs", Context.MODE_PRIVATE)

    fun getRestrictedPackages(): Set<String> {
        return prefs.getStringSet("restricted_packages", setOf()) ?: setOf()
    }

    fun addRestrictedPackage(packageName: String) {
        val current = getRestrictedPackages().toMutableSet()
        current.add(packageName)
        prefs.edit().putStringSet("restricted_packages", current).apply()
    }

    fun removeRestrictedPackage(packageName: String) {
        val current = getRestrictedPackages().toMutableSet()
        current.remove(packageName)
        prefs.edit().putStringSet("restricted_packages", current).apply()
    }
}
