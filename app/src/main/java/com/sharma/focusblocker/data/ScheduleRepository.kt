package com.sharma.focusblocker.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScheduleRepository(
    private val scheduleDao: ScheduleDao,
    private val blockerPreferences: BlockerPreferences
) {
    val schedulesFlow: Flow<List<Schedule>> = scheduleDao.getAllSchedulesFlow()

    suspend fun getSchedulesSnapshot(): List<Schedule> {
        return scheduleDao.getAllSchedules()
    }

    suspend fun insertSchedule(schedule: Schedule) {
        scheduleDao.insertSchedule(schedule)
    }

    suspend fun updateSchedule(schedule: Schedule) {
        scheduleDao.updateSchedule(schedule)
    }

    suspend fun deleteSchedule(schedule: Schedule) {
        scheduleDao.deleteSchedule(schedule)
    }

    suspend fun migrateFromPreferencesIfNeeded() {
        withContext(Dispatchers.IO) {
            val hasMigrated = blockerPreferences.hasMigratedToRoom()
            if (!hasMigrated) {
                val oldSchedules = blockerPreferences.getSchedules()
                if (oldSchedules.isNotEmpty()) {
                    scheduleDao.insertSchedules(oldSchedules)
                }
                blockerPreferences.setMigratedToRoom(true)
            }
        }
    }
}
