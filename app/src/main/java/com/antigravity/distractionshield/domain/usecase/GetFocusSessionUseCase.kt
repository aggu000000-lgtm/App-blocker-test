package com.antigravity.distractionshield.domain.usecase

import com.antigravity.distractionshield.domain.model.FocusSession
import com.antigravity.distractionshield.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow

class GetFocusSessionUseCase(
    private val sessionRepository: SessionRepository
) {
    operator fun invoke(): Flow<FocusSession> {
        return sessionRepository.focusSession
    }
}
