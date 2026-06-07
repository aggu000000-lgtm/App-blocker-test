package com.antigravity.distractionshield.domain.repository

import kotlinx.coroutines.flow.Flow

interface BlockedAppsRepository {
    val blockedApps: Flow<Set<String>>
    fun getBlockedApps(): Set<String>
    suspend fun addBlockedApp(packageName: String)
    suspend fun removeBlockedApp(packageName: String)
}
