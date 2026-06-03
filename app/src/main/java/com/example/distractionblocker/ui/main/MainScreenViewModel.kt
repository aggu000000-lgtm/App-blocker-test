package com.example.distractionblocker.ui.main

import android.app.Application
import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.distractionblocker.AppBlockerService
import com.example.distractionblocker.BlockedAppsManager
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
    val isAccessibilityEnabled: Boolean = false,
    val isSessionActive: Boolean = false,
    val sessionEndTime: Long = 0L,
    val sessionTotalDuration: Long = 0L,
    val showExtensionPrompt: Boolean = false,
    val isLoading: Boolean = true
)

class MainScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val blockedAppsManager = BlockedAppsManager(context)
    private val packageManager: PackageManager = context.packageManager

    private val _uiState = MutableStateFlow(MainScreenState())
    val uiState: StateFlow<MainScreenState> = _uiState.asStateFlow()

    // Cache of all loaded launcher apps
    private var allApps: List<AppInfo> = emptyList()

    init {
        loadApps()
        startStatusTicker()
    }

    /**
     * Loads installed apps containing launcher intents in the background.
     */
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

    /**
     * Filters apps based on search query.
     */
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

    /**
     * Toggles the blocked state of a package.
     */
    fun toggleAppBlock(packageName: String) {
        val currentApp = allApps.find { it.packageName == packageName } ?: return
        val newBlockedState = !currentApp.isBlocked

        if (newBlockedState) {
            blockedAppsManager.addBlockedApp(packageName)
        } else {
            blockedAppsManager.removeBlockedApp(packageName)
        }

        // Update list cache and UI state
        allApps = allApps.map {
            if (it.packageName == packageName) it.copy(isBlocked = newBlockedState) else it
        }
        _uiState.update { it.copy(installedApps = allApps) }
        filterApps(_uiState.value.searchQuery)
    }

    /**
     * Starts a focus session.
     */
    fun startFocusSession(durationMillis: Long) {
        blockedAppsManager.startSession(durationMillis)
        _uiState.update {
            it.copy(
                isSessionActive = true,
                sessionEndTime = blockedAppsManager.getSessionEndTime(),
                sessionTotalDuration = blockedAppsManager.getSessionTotalDuration(),
                showExtensionPrompt = false
            )
        }
    }

    /**
     * Extends the active/expired focus session.
     */
    fun extendFocusSession(durationMillis: Long) {
        blockedAppsManager.extendSession(durationMillis)
        _uiState.update {
            it.copy(
                isSessionActive = true,
                sessionEndTime = blockedAppsManager.getSessionEndTime(),
                sessionTotalDuration = blockedAppsManager.getSessionTotalDuration(),
                showExtensionPrompt = false
            )
        }
    }

    /**
     * Ends the focus session.
     */
    fun endFocusSession() {
        blockedAppsManager.endSession()
        _uiState.update {
            it.copy(
                isSessionActive = false,
                sessionEndTime = 0L,
                sessionTotalDuration = 0L,
                showExtensionPrompt = false
            )
        }
    }

    /**
     * Dismisses the expired session extension prompt.
     */
    fun dismissExtensionPrompt() {
        _uiState.update { it.copy(showExtensionPrompt = false) }
    }

    /**
     * Directs the user to the accessibility settings screen.
     */
    fun openAccessibilitySettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback
        }
    }

    /**
     * Periodically updates service statuses and timer countdown states.
     */
    private fun startStatusTicker() {
        viewModelScope.launch {
            var wasSessionActiveLastCheck = blockedAppsManager.isSessionActive()

            while (true) {
                val isServiceEnabled = isAccessibilityServiceEnabled(context, AppBlockerService::class.java)
                val isActiveNow = blockedAppsManager.isSessionActive()
                val sessionEndTime = blockedAppsManager.getSessionEndTime()

                // Detect when session has transitioned from active to inactive (expired)
                val showPrompt = if (wasSessionActiveLastCheck && !isActiveNow && sessionEndTime > 0) {
                    true
                } else {
                    _uiState.value.showExtensionPrompt
                }

                wasSessionActiveLastCheck = isActiveNow

                _uiState.update {
                    it.copy(
                        isAccessibilityEnabled = isServiceEnabled,
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

    /**
     * Checks if a specific AccessibilityService component is enabled.
     */
    private fun isAccessibilityServiceEnabled(context: Context, service: Class<out AccessibilityService>): Boolean {
        val expectedComponentName = ComponentName(context, service)
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServices)
        while (colonSplitter.hasNext()) {
            val componentNameString = colonSplitter.next()
            val enabledService = ComponentName.unflattenFromString(componentNameString)
            if (enabledService != null && enabledService == expectedComponentName) {
                return true
            }
        }
        return false
    }
}
