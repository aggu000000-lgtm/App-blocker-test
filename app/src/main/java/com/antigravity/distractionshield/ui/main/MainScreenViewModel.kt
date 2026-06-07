package com.antigravity.distractionshield.ui.main

import android.app.AppOpsManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.antigravity.distractionshield.AppBlockerForegroundService
import com.antigravity.distractionshield.BlockedAppsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class AppInfo(
    val name: String,
    val packageName: String,
    val isBlocked: Boolean
)

data class MainScreenState(
    val installedApps: List<AppInfo> = emptyList(),
    val filteredApps: List<AppInfo> = emptyList(),
    val searchQuery: String = "",
    val isUsageStatsGranted: Boolean = false,
    val isSessionActive: Boolean = false,
    val sessionEndTime: Long = 0L,
    val sessionTotalDuration: Long = 0L,
    val showExtensionPrompt: Boolean = false,
    val showUsageStatsDisclosure: Boolean = false,
    val isLoading: Boolean = true
)

class MainScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val blockedAppsManager = BlockedAppsManager(context)
    private val packageManager: PackageManager = context.packageManager

    private val _uiState = MutableStateFlow(MainScreenState())
    val uiState: StateFlow<MainScreenState> = _uiState.asStateFlow()

    private var allApps: List<AppInfo> = emptyList()

    init {
        loadApps()
        startStatusTicker()
        if (blockedAppsManager.isSessionActive()) {
            startService()
        }
    }

    fun loadApps() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val apps = withContext(Dispatchers.IO) {
                try {
                    val packages = packageManager.getInstalledPackages(0)
                    val blockedSet = blockedAppsManager.getBlockedApps()

                    packages.mapNotNull { pkg ->
                        val launchIntent = packageManager.getLaunchIntentForPackage(pkg.packageName)
                        if (launchIntent != null && pkg.packageName != context.packageName) {
                            val name = pkg.applicationInfo?.loadLabel(packageManager)?.toString() ?: pkg.packageName
                            AppInfo(
                                name = name,
                                packageName = pkg.packageName,
                                isBlocked = blockedSet.contains(pkg.packageName)
                            )
                        } else {
                            null
                        }
                    }.sortedBy { it.name }
                } catch (e: Exception) {
                    emptyList()
                }
            }
            allApps = apps
            _uiState.update {
                it.copy(
                    installedApps = apps,
                    isLoading = false
                )
            }
            filterApps(_uiState.value.searchQuery)
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        filterApps(query)
    }

    private fun filterApps(query: String) {
        val filtered = if (query.isEmpty()) {
            allApps
        } else {
            allApps.filter { it.name.contains(query, ignoreCase = true) || it.packageName.contains(query, ignoreCase = true) }
        }
        _uiState.update { it.copy(filteredApps = filtered) }
    }

    fun toggleAppBlock(packageName: String) {
        val currentApp = allApps.find { it.packageName == packageName } ?: return
        val newBlockedState = !currentApp.isBlocked

        if (newBlockedState) {
            blockedAppsManager.addBlockedApp(packageName)
        } else {
            blockedAppsManager.removeBlockedApp(packageName)
        }

        allApps = allApps.map {
            if (it.packageName == packageName) it.copy(isBlocked = newBlockedState) else it
        }
        _uiState.update { it.copy(installedApps = allApps) }
        filterApps(_uiState.value.searchQuery)
    }

    fun startFocusSession(durationMillis: Long) {
        blockedAppsManager.startSession(durationMillis)
        startService()
        _uiState.update {
            it.copy(
                isSessionActive = true,
                sessionEndTime = blockedAppsManager.getSessionEndTime(),
                sessionTotalDuration = blockedAppsManager.getSessionTotalDuration(),
                showExtensionPrompt = false
            )
        }
    }

    fun extendFocusSession(durationMillis: Long) {
        blockedAppsManager.extendSession(durationMillis)
        startService()
        _uiState.update {
            it.copy(
                isSessionActive = true,
                sessionEndTime = blockedAppsManager.getSessionEndTime(),
                sessionTotalDuration = blockedAppsManager.getSessionTotalDuration(),
                showExtensionPrompt = false
            )
        }
    }

    fun endFocusSession() {
        blockedAppsManager.endSession()
        stopService()
        _uiState.update {
            it.copy(
                isSessionActive = false,
                sessionEndTime = 0L,
                sessionTotalDuration = 0L,
                showExtensionPrompt = false
            )
        }
    }

    fun dismissExtensionPrompt() {
        _uiState.update { it.copy(showExtensionPrompt = false) }
    }

    fun showUsageStatsDisclosure() {
        _uiState.update { it.copy(showUsageStatsDisclosure = true) }
    }

    fun dismissUsageStatsDisclosure() {
        _uiState.update { it.copy(showUsageStatsDisclosure = false) }
    }

    fun openUsageStatsSettings(context: Context) {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            intent.data = Uri.parse("package:${context.packageName}")
            context.startActivity(intent)
        } catch (e: Exception) {
            intent.data = null
            context.startActivity(intent)
        }
    }

    private fun startService() {
        val intent = Intent(context, AppBlockerForegroundService::class.java).apply {
            action = AppBlockerForegroundService.ACTION_START
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        } catch (e: Exception) {
            Log.e("MainScreenViewModel", "Failed to start foreground service", e)
        }
    }

    private fun stopService() {
        val intent = Intent(context, AppBlockerForegroundService::class.java).apply {
            action = AppBlockerForegroundService.ACTION_STOP
        }
        try {
            context.startService(intent)
        } catch (e: Exception) {
            Log.e("MainScreenViewModel", "Failed to stop foreground service", e)
        }
    }

    private fun startStatusTicker() {
        viewModelScope.launch {
            var wasSessionActiveLastCheck = blockedAppsManager.isSessionActive()

            while (true) {
                val isPermissionGranted = isUsageStatsPermissionGranted(context)
                val isActiveNow = blockedAppsManager.isSessionActive()
                val sessionEndTime = blockedAppsManager.getSessionEndTime()

                val showPrompt = if (wasSessionActiveLastCheck && !isActiveNow && sessionEndTime > 0) {
                    true
                } else {
                    _uiState.value.showExtensionPrompt
                }

                if (wasSessionActiveLastCheck && !isActiveNow) {
                    stopService()
                }

                wasSessionActiveLastCheck = isActiveNow

                _uiState.update {
                    it.copy(
                        isUsageStatsGranted = isPermissionGranted,
                        isSessionActive = isActiveNow,
                        sessionEndTime = sessionEndTime,
                        sessionTotalDuration = blockedAppsManager.getSessionTotalDuration(),
                        showExtensionPrompt = showPrompt
                    )
                }
                delay(1000L)
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun isUsageStatsPermissionGranted(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager ?: return false
        val mode = appOps.noteOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }
}
