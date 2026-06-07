package com.antigravity.distractionshield.domain.usecase

import com.antigravity.distractionshield.domain.repository.BlockedAppsRepository
import kotlinx.coroutines.flow.Flow

class GetBlockedAppsUseCase(
    private val blockedAppsRepository: BlockedAppsRepository
) {
    operator fun invoke(): Flow<Set<String>> {
        return blockedAppsRepository.blockedApps
    }
}
