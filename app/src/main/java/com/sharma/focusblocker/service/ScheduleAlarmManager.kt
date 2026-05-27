package com.sharma.focusblocker.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

object ScheduleAlarmManager {
    fun updateAlarms(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = com.sharma.focusblocker.data.AppDatabase.getDatabase(context)
            val schedules = db.scheduleDao().getAllSchedules()
            
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent("com.sharma.focusblocker.ACTION_CHECK_SCHEDULE")
            intent.setPackage(context.packageName)
            
            val pendingIntent = PendingIntent.getBroadcast(
                context, 
                0, 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val nextTime = getNextStateChangeTime(schedules)
            if (nextTime != null) {
                try {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                        if (alarmManager.canScheduleExactAlarms()) {
                            alarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                nextTime,
                                pendingIntent
                            )
                        }
                    } else {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            nextTime,
                            pendingIntent
                        )
                    }
                } catch (e: SecurityException) {
                    // Ignore if permission isn't granted
                }
            } else {
                alarmManager.cancel(pendingIntent)
            }
        }
    }
    
    private fun getNextStateChangeTime(schedules: List<com.sharma.focusblocker.data.Schedule>): Long? {
        val now = System.currentTimeMillis()
        var nextTime: Long? = null
        
        // Check for the next 7 days to find the closest start or end time
        for (i in 0..7) {
            for (schedule in schedules) {
                if (!schedule.isEnabled) continue
                
                val cal = Calendar.getInstance()
                cal.timeInMillis = now
                cal.add(Calendar.DAY_OF_YEAR, i)
                val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
                
                // If the schedule applies to this day, check its start time
                if (schedule.daysOfWeek.contains(dayOfWeek)) {
                    cal.set(Calendar.HOUR_OF_DAY, schedule.startHour)
                    cal.set(Calendar.MINUTE, schedule.startMinute)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                    val startTime = cal.timeInMillis
                    
                    if (startTime > now) {
                        if (nextTime == null || startTime < nextTime) nextTime = startTime
                    }
                    
                    val endCal = Calendar.getInstance()
                    endCal.timeInMillis = now
                    endCal.add(Calendar.DAY_OF_YEAR, i)
                    if (schedule.startHour > schedule.endHour || (schedule.startHour == schedule.endHour && schedule.startMinute > schedule.endMinute)) {
                        // Crosses midnight, so the end time is on the next day
                        endCal.add(Calendar.DAY_OF_YEAR, 1)
                    }
                    endCal.set(Calendar.HOUR_OF_DAY, schedule.endHour)
                    endCal.set(Calendar.MINUTE, schedule.endMinute)
                    endCal.set(Calendar.SECOND, 0)
                    endCal.set(Calendar.MILLISECOND, 0)
                    val endTime = endCal.timeInMillis
                    if (endTime > now) {
                        if (nextTime == null || endTime < nextTime) nextTime = endTime
                    }
                }
            }
        }
        return nextTime
    }
}
