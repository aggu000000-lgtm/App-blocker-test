package com.sharma.focusblocker.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sharma.focusblocker.data.Schedule
import com.sharma.focusblocker.data.ScheduleRepository
import com.sharma.focusblocker.service.ScheduleAlarmManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val application: Application,
    private val repository: ScheduleRepository
) : ViewModel() {

    val schedules: StateFlow<List<Schedule>> = repository.schedulesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            repository.migrateFromPreferencesIfNeeded()
        }
    }

    fun toggleSchedule(schedule: Schedule, isEnabled: Boolean) {
        val updated = schedule.copy(isEnabled = isEnabled)
        viewModelScope.launch {
            repository.updateSchedule(updated)
            ScheduleAlarmManager.updateAlarms(application)
        }
    }

    fun deleteSchedule(schedule: Schedule) {
        viewModelScope.launch {
            repository.deleteSchedule(schedule)
            ScheduleAlarmManager.updateAlarms(application)
        }
    }

    fun saveSchedule(schedule: Schedule) {
        viewModelScope.launch {
            repository.insertSchedule(schedule)
            ScheduleAlarmManager.updateAlarms(application)
        }
    }
}
