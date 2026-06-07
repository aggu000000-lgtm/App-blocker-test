package com.antigravity.distractionshield.domain.usecase

import com.antigravity.distractionshield.domain.repository.SessionRepository

class EndFocusSessionUseCase(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke() {
        sessionRepository.endSession()
    }
}
