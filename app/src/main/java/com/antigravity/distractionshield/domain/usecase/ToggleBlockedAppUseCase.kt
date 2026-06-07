package com.antigravity.distractionshield.domain.usecase

import com.antigravity.distractionshield.domain.repository.BlockedAppsRepository

class ToggleBlockedAppUseCase(
    private val blockedAppsRepository: BlockedAppsRepository
) {
    suspend operator fun invoke(packageName: String) {
        val currentBlocked = blockedAppsRepository.getBlockedApps()
        if (currentBlocked.contains(packageName)) {
            blockedAppsRepository.removeBlockedApp(packageName)
        } else {
            blockedAppsRepository.addBlockedApp(packageName)
        }
    }
}
