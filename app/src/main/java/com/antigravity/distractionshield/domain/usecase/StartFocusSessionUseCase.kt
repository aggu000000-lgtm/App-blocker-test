package com.antigravity.distractionshield.domain.usecase

import com.antigravity.distractionshield.domain.repository.SessionRepository

class StartFocusSessionUseCase(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(durationMillis: Long) {
        sessionRepository.startSession(durationMillis)
    }
}
