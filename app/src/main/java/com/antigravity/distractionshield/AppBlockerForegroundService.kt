package com.antigravity.distractionshield

import android.app.*
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class AppBlockerForegroundService : Service() {

    private lateinit var blockedAppsManager: BlockedAppsManager
    private var serviceJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    companion object {
        private const val CHANNEL_ID = "app_blocker_service_channel"
        private const val NOTIFICATION_ID = 1001
        private const val TICK_INTERVAL_MS = 500L
        
        const val ACTION_START = "ACTION_START_BLOCKER"
        const val ACTION_STOP = "ACTION_STOP_BLOCKER"
    }

    override fun onCreate() {
        super.onCreate()
        blockedAppsManager = BlockedAppsManager(applicationContext)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: ACTION_START
        
        if (action == ACTION_STOP) {
            stopService()
            return START_NOT_STICKY
        }

        startForegroundServiceCompat()
        startMonitoring()

        return START_STICKY
    }

    private fun startForegroundServiceCompat() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Active Focus Shield Enabled")
            .setContentText("Monitoring distracting apps to keep you focused.")
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun startMonitoring() {
        if (serviceJob?.isActive == true) return

        serviceJob = serviceScope.launch {
            while (isActive) {
                if (!blockedAppsManager.isSessionActive()) {
                    withContext(Dispatchers.Main) {
                        stopService()
                    }
                    break
                }

                checkForegroundApp()
                delay(TICK_INTERVAL_MS)
            }
        }
    }

    private fun checkForegroundApp() {
        val foregroundPackage = getForegroundPackage(applicationContext) ?: return
        
        if (foregroundPackage == packageName) return

        val blockedApps = blockedAppsManager.getBlockedApps()
        if (blockedApps.contains(foregroundPackage)) {
            Log.d("AppBlockerForegroundService", "Intercepted: $foregroundPackage. Redirecting to BlockerActivity.")
            
            val intent = Intent(this, BlockerActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                putExtra(BlockerActivity.EXTRA_BLOCKED_PACKAGE, foregroundPackage)
            }
            startActivity(intent)
        }
    }

    private fun getForegroundPackage(context: Context): String? {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager ?: return null
        val time = System.currentTimeMillis()
        
        val usageEvents = usageStatsManager.queryEvents(time - 10000, time) ?: return null
        val event = UsageEvents.Event()
        var lastForegroundApp: String? = null
        
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                lastForegroundApp = event.packageName
            }
        }
        return lastForegroundApp
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Focus Shield Service Channel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Used to display status of the active focus session app blocker"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun stopService() {
        serviceJob?.cancel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        stopSelf()
    }

    override fun onDestroy() {
        serviceJob?.cancel()
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
