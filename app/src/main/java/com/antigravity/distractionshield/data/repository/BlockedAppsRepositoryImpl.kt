package com.antigravity.distractionshield.data.repository

import com.antigravity.distractionshield.data.datasource.PreferencesDataSource
import com.antigravity.distractionshield.domain.repository.BlockedAppsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class BlockedAppsRepositoryImpl(
    private val dataSource: PreferencesDataSource
) : BlockedAppsRepository {
    override val blockedApps: Flow<Set<String>> = dataSource.preferenceChangesFlow
        .filter { it == PreferencesDataSource.KEY_BLOCKED_PACKAGES }
        .onStart { emit("") }
        .map { dataSource.getBlockedApps() }
        .distinctUntilChanged()

    override fun getBlockedApps(): Set<String> {
        return dataSource.getBlockedApps()
    }

    override suspend fun addBlockedApp(packageName: String) {
        dataSource.addBlockedApp(packageName)
    }

    override suspend fun removeBlockedApp(packageName: String) {
        dataSource.removeBlockedApp(packageName)
    }
}
