package com.antigravity.distractionshield.domain.usecase

import com.antigravity.distractionshield.domain.repository.AppRepository

class OpenUsageStatsSettingsUseCase(
    private val appRepository: AppRepository
) {
    operator fun invoke() {
        appRepository.openUsageStatsSettings()
    }
}
