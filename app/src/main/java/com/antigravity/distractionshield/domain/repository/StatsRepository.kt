package com.antigravity.distractionshield.domain.repository

import com.antigravity.distractionshield.domain.model.DailyStats
import kotlinx.coroutines.flow.Flow

interface StatsRepository {
    val weeklyStatsFlow: Flow<List<DailyStats>>
    fun getDailyStats(dateStr: String): DailyStats
    fun getWeeklyStats(): List<DailyStats>
    fun incrementFocusDuration(dateStr: String, durationMs: Long)
    fun incrementBlockedAttempts(dateStr: String)
    fun incrementRapidSwitches(dateStr: String)
}
