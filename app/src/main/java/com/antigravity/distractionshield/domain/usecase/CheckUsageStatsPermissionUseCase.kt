package com.antigravity.distractionshield.domain.usecase

import com.antigravity.distractionshield.domain.repository.AppRepository

class CheckUsageStatsPermissionUseCase(
    private val appRepository: AppRepository
) {
    operator fun invoke(): Boolean {
        return appRepository.isUsageStatsPermissionGranted()
    }
}
