package com.sharma.focusblocker.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import com.sharma.focusblocker.data.BlockerPreferences
import com.sharma.focusblocker.data.Schedule
import com.sharma.focusblocker.service.ScheduleAlarmManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val application: android.app.Application
) : ViewModel() {
    private val prefs = BlockerPreferences(application)
    
    private val _schedules = MutableStateFlow<List<Schedule>>(emptyList())
    val schedules: StateFlow<List<Schedule>> = _schedules
    
    init {
        _schedules.value = prefs.getSchedules()
    }
    
    fun toggleSchedule(schedule: Schedule, isEnabled: Boolean) {
        val updated = schedule.copy(isEnabled = isEnabled)
        updateSchedule(updated)
    }
    
    fun deleteSchedule(schedule: Schedule) {
        val newList = _schedules.value.filter { it.id != schedule.id }
        saveList(newList)
    }
    
    fun saveSchedule(schedule: Schedule) {
        val current = _schedules.value.toMutableList()
        val index = current.indexOfFirst { it.id == schedule.id }
        if (index != -1) {
            current[index] = schedule
        } else {
            current.add(schedule)
        }
        saveList(current)
    }
    
    private fun updateSchedule(schedule: Schedule) {
        saveSchedule(schedule)
    }
    
    private fun saveList(list: List<Schedule>) {
        _schedules.value = list
        prefs.saveSchedules(list)
        ScheduleAlarmManager.updateAlarms(application)
    }
}
