package com.sharma.focusblocker.service

import com.sharma.focusblocker.data.Schedule
import java.util.Calendar

object ScheduleChecker {
    fun getActiveBlockedPackages(schedules: List<Schedule>, currentTimeMillis: Long = System.currentTimeMillis()): Set<String> {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTimeMillis
        
        val currentDay = calendar.get(Calendar.DAY_OF_WEEK)
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val currentMinutesOfDay = currentHour * 60 + currentMinute
        
        val blocked = mutableSetOf<String>()
        
        for (schedule in schedules) {
            if (!schedule.isEnabled) continue
            
            val startMinutes = schedule.startHour * 60 + schedule.startMinute
            val endMinutes = schedule.endHour * 60 + schedule.endMinute
            
            var isActive = false
            
            if (startMinutes <= endMinutes) {
                // Normal schedule
                if (schedule.daysOfWeek.contains(currentDay) && currentMinutesOfDay in startMinutes..endMinutes) {
                    isActive = true
                }
            } else {
                // Crosses midnight
                val previousDay = if (currentDay == Calendar.SUNDAY) Calendar.SATURDAY else currentDay - 1
                
                if (schedule.daysOfWeek.contains(currentDay) && currentMinutesOfDay >= startMinutes) {
                    isActive = true
                } else if (schedule.daysOfWeek.contains(previousDay) && currentMinutesOfDay <= endMinutes) {
                    isActive = true
                }
            }
            
            if (isActive) {
                blocked.addAll(schedule.blockedPackages)
            }
        }
        
        return blocked
    }
}
