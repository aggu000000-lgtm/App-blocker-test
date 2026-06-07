package com.antigravity.distractionshield.domain.repository

import com.antigravity.distractionshield.domain.model.FocusSession
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    val focusSession: Flow<FocusSession>
    fun isSessionActive(): Boolean
    fun getSessionEndTime(): Long
    fun getSessionTotalDuration(): Long
    suspend fun startSession(durationMillis: Long)
    suspend fun extendSession(durationMillis: Long)
    suspend fun endSession()
}
