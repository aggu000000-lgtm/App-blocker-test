package com.antigravity.distractionshield

import android.app.*
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.antigravity.distractionshield.di.DependencyProvider
import com.antigravity.distractionshield.domain.repository.BlockedAppsRepository
import com.antigravity.distractionshield.domain.repository.SessionRepository
import com.antigravity.distractionshield.domain.repository.StatsRepository
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AppBlockerForegroundService : Service() {

    private lateinit var sessionRepository: SessionRepository
    private lateinit var blockedAppsRepository: BlockedAppsRepository
    private lateinit var statsRepository: StatsRepository

    private var serviceJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private var lastForegroundApp: String? = null
    private var currentAppStartTime: Long = System.currentTimeMillis()
    private var lastProcessedEventTime: Long = System.currentTimeMillis()
    private val homePackages = mutableSetOf<String>()

    companion object {
        private const val CHANNEL_ID = "app_blocker_service_channel"
        private const val NOTIFICATION_ID = 1001
        private const val TICK_INTERVAL_MS = 500L
        
        const val ACTION_START = "ACTION_START_BLOCKER"
        const val ACTION_STOP = "ACTION_STOP_BLOCKER"
    }

    override fun onCreate() {
        super.onCreate()
        sessionRepository = DependencyProvider.sessionRepository
        blockedAppsRepository = DependencyProvider.blockedAppsRepository
        statsRepository = DependencyProvider.statsRepository
        createNotificationChannel()
        loadHomePackages()
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

    private fun loadHomePackages() {
        homePackages.clear()
        try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
            }
            val resolveInfos = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            for (info in resolveInfos) {
                info.activityInfo?.packageName?.let { homePackages.add(it) }
            }
        } catch (e: Exception) {
            Log.e("AppBlockerForegroundService", "Error loading home packages", e)
        }
    }

    private fun initializeTrackingState() {
        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager ?: return
        val time = System.currentTimeMillis()
        val usageEvents = usageStatsManager.queryEvents(time - 5 * 60 * 1000L, time) ?: return
        val event = UsageEvents.Event()
        var lastApp: String? = null
        var lastTime: Long = 0L
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                lastApp = event.packageName
                lastTime = event.timeStamp
            }
        }
        lastForegroundApp = lastApp
        currentAppStartTime = if (lastTime > 0L) lastTime else time
        lastProcessedEventTime = time
        Log.d("AppBlockerForegroundService", "Seeded tracking state: app=$lastForegroundApp, start=$currentAppStartTime")
    }

    private fun startMonitoring() {
        if (serviceJob?.isActive == true) return

        initializeTrackingState()

        serviceJob = serviceScope.launch {
            while (isActive) {
                if (!sessionRepository.isSessionActive()) {
                    withContext(Dispatchers.Main) {
                        stopService()
                    }
                    break
                }

                checkForegroundApp()

                // Increment focus duration: increment by TICK_INTERVAL_MS on each tick
                // if the current app is not blocked and not home/self.
                val currentApp = lastForegroundApp
                if (currentApp != null && !isLauncherOrSelf(currentApp)) {
                    val blockedApps = blockedAppsRepository.getBlockedApps()
                    if (!blockedApps.contains(currentApp)) {
                        statsRepository.incrementFocusDuration(getCurrentDateStr(), TICK_INTERVAL_MS)
                    }
                }

                delay(TICK_INTERVAL_MS)
            }
        }
    }

    private fun checkForegroundApp() {
        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager ?: return
        val time = System.currentTimeMillis()
        
        val usageEvents = usageStatsManager.queryEvents(lastProcessedEventTime, time) ?: return
        val event = UsageEvents.Event()
        
        var hasNewEvents = false
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            val eventTime = event.timeStamp
            if (eventTime > lastProcessedEventTime) {
                lastProcessedEventTime = eventTime
                
                if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                    val newPackage = event.packageName
                    handleAppSwitch(newPackage, eventTime)
                    hasNewEvents = true
                }
            }
        }
        
        if (!hasNewEvents) {
            lastForegroundApp?.let { checkAndBlockApp(it) }
        }
    }

    private fun handleAppSwitch(newPackage: String, eventTime: Long) {
        val oldPackage = lastForegroundApp
        val startTime = currentAppStartTime
        
        lastForegroundApp = newPackage
        currentAppStartTime = eventTime
        
        if (oldPackage != null && oldPackage != newPackage) {
            val durationMs = eventTime - startTime
            if (durationMs in 500L..10000L) {
                if (!isLauncherOrSelf(oldPackage) && !isLauncherOrSelf(newPackage)) {
                    Log.d("AppBlockerForegroundService", "Rapid app-switch detected: from $oldPackage to $newPackage (duration: ${durationMs}ms)")
                    statsRepository.incrementRapidSwitches(getCurrentDateStr())
                }
            }
        }
        
        checkAndBlockApp(newPackage)
    }

    private fun checkAndBlockApp(pkgName: String) {
        if (pkgName == packageName) return
        
        val blockedApps = blockedAppsRepository.getBlockedApps()
        if (blockedApps.contains(pkgName)) {
            Log.d("AppBlockerForegroundService", "Intercepted: $pkgName. Redirecting to BlockerActivity.")
            
            statsRepository.incrementBlockedAttempts(getCurrentDateStr())

            val intent = Intent(this, BlockerActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                putExtra(BlockerActivity.EXTRA_BLOCKED_PACKAGE, pkgName)
            }
            startActivity(intent)
        }
    }

    private fun isLauncherOrSelf(pkgName: String): Boolean {
        if (pkgName == packageName) return true
        return homePackages.contains(pkgName)
    }

    private fun getCurrentDateStr(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Calendar.getInstance().time)
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
