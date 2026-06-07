package com.antigravity.distractionshield.domain.usecase

import com.antigravity.distractionshield.domain.repository.SessionRepository

class ExtendFocusSessionUseCase(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(durationMillis: Long) {
        sessionRepository.extendSession(durationMillis)
    }
}
