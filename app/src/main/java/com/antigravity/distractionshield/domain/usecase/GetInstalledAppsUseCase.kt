package com.antigravity.distractionshield.domain.usecase

import com.antigravity.distractionshield.domain.model.AppInfo
import com.antigravity.distractionshield.domain.repository.AppRepository
import com.antigravity.distractionshield.domain.repository.BlockedAppsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetInstalledAppsUseCase(
    private val appRepository: AppRepository,
    private val blockedAppsRepository: BlockedAppsRepository
) {
    operator fun invoke(): Flow<List<AppInfo>> {
        return blockedAppsRepository.blockedApps.map { blockedPackages ->
            val installed = appRepository.getInstalledApps()
            installed.map { app ->
                app.copy(isBlocked = blockedPackages.contains(app.packageName))
            }
        }
    }
}
