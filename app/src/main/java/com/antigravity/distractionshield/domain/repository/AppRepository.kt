package com.antigravity.distractionshield.domain.repository

import com.antigravity.distractionshield.domain.model.AppInfo

interface AppRepository {
    suspend fun getInstalledApps(): List<AppInfo>
    fun isUsageStatsPermissionGranted(): Boolean
    fun openUsageStatsSettings()
}
