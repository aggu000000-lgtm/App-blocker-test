package com.antigravity.distractionshield.data.repository

import com.antigravity.distractionshield.data.datasource.PreferencesDataSource
import com.antigravity.distractionshield.domain.model.DailyStats
import com.antigravity.distractionshield.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.text.SimpleDateFormat
import java.util.*

class StatsRepositoryImpl(
    private val dataSource: PreferencesDataSource
) : StatsRepository {

    override val weeklyStatsFlow: Flow<List<DailyStats>> = dataSource.preferenceChangesFlow
        .filter { it.startsWith("stats_") }
        .onStart { emit("") }
        .map { getWeeklyStats() }
        .distinctUntilChanged()

    override fun getDailyStats(dateStr: String): DailyStats {
        return DailyStats(
            dateStr = dateStr,
            focusDurationMs = dataSource.getDailyFocusDuration(dateStr),
            blockedAttempts = dataSource.getDailyBlockedAttempts(dateStr),
            rapidSwitches = dataSource.getDailyRapidSwitches(dateStr)
        )
    }

    override fun getWeeklyStats(): List<DailyStats> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val stats = mutableListOf<DailyStats>()
        
        for (i in 0 until 7) {
            val dateStr = dateFormat.format(calendar.time)
            stats.add(getDailyStats(dateStr))
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        
        return stats.reversed()
    }

    override fun incrementFocusDuration(dateStr: String, durationMs: Long) {
        dataSource.incrementDailyFocusDuration(dateStr, durationMs)
    }

    override fun incrementBlockedAttempts(dateStr: String) {
        dataSource.incrementDailyBlockedAttempts(dateStr)
    }

    override fun incrementRapidSwitches(dateStr: String) {
        dataSource.incrementDailyRapidSwitches(dateStr)
    }
}
