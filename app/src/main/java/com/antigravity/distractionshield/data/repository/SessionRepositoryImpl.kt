package com.antigravity.distractionshield.data.repository

import com.antigravity.distractionshield.data.datasource.PreferencesDataSource
import com.antigravity.distractionshield.domain.model.FocusSession
import com.antigravity.distractionshield.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class SessionRepositoryImpl(
    private val dataSource: PreferencesDataSource
) : SessionRepository {
    override val focusSession: Flow<FocusSession> = dataSource.preferenceChangesFlow
        .filter { it == PreferencesDataSource.KEY_SESSION_END_TIME || it == PreferencesDataSource.KEY_SESSION_TOTAL_DURATION }
        .onStart { emit("") }
        .map {
            val endTime = dataSource.getSessionEndTime()
            val isActive = endTime > 0 && System.currentTimeMillis() < endTime
            FocusSession(
                isActive = isActive,
                endTime = endTime,
                totalDuration = dataSource.getSessionTotalDuration()
            )
        }
        .distinctUntilChanged()

    override fun isSessionActive(): Boolean {
        val endTime = dataSource.getSessionEndTime()
        return endTime > 0 && System.currentTimeMillis() < endTime
    }

    override fun getSessionEndTime(): Long = dataSource.getSessionEndTime()

    override fun getSessionTotalDuration(): Long = dataSource.getSessionTotalDuration()

    override suspend fun startSession(durationMillis: Long) {
        dataSource.startSession(durationMillis)
    }

    override suspend fun extendSession(durationMillis: Long) {
        dataSource.extendSession(durationMillis)
    }

    override suspend fun endSession() {
        dataSource.endSession()
    }
}
