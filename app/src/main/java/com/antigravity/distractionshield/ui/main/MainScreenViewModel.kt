package com.antigravity.distractionshield.ui.main

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.antigravity.distractionshield.AppBlockerForegroundService
import com.antigravity.distractionshield.di.DependencyProvider
import com.antigravity.distractionshield.domain.model.AppInfo
import com.antigravity.distractionshield.domain.model.ThemeSettings
import com.antigravity.distractionshield.domain.usecase.CheckUsageStatsPermissionUseCase
import com.antigravity.distractionshield.domain.usecase.EndFocusSessionUseCase
import com.antigravity.distractionshield.domain.usecase.ExtendFocusSessionUseCase
import com.antigravity.distractionshield.domain.usecase.GetFocusSessionUseCase
import com.antigravity.distractionshield.domain.usecase.GetInstalledAppsUseCase
import com.antigravity.distractionshield.domain.usecase.GetThemeSettingsUseCase
import com.antigravity.distractionshield.domain.usecase.OpenUsageStatsSettingsUseCase
import com.antigravity.distractionshield.domain.usecase.SetThemeSettingsUseCase
import com.antigravity.distractionshield.domain.usecase.StartFocusSessionUseCase
import com.antigravity.distractionshield.domain.usecase.ToggleBlockedAppUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

class MainScreenViewModel @JvmOverloads constructor(
    application: Application,
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase = DependencyProvider.getInstalledAppsUseCase,
    private val toggleBlockedAppUseCase: ToggleBlockedAppUseCase = DependencyProvider.toggleBlockedAppUseCase,
    private val startFocusSessionUseCase: StartFocusSessionUseCase = DependencyProvider.startFocusSessionUseCase,
    private val extendFocusSessionUseCase: ExtendFocusSessionUseCase = DependencyProvider.extendFocusSessionUseCase,
    private val endFocusSessionUseCase: EndFocusSessionUseCase = DependencyProvider.endFocusSessionUseCase,
    private val getFocusSessionUseCase: GetFocusSessionUseCase = DependencyProvider.getFocusSessionUseCase,
    private val checkUsageStatsPermissionUseCase: CheckUsageStatsPermissionUseCase = DependencyProvider.checkUsageStatsPermissionUseCase,
    private val openUsageStatsSettingsUseCase: OpenUsageStatsSettingsUseCase = DependencyProvider.openUsageStatsSettingsUseCase,
    private val getThemeSettingsUseCase: GetThemeSettingsUseCase = DependencyProvider.getThemeSettingsUseCase,
    private val setThemeSettingsUseCase: SetThemeSettingsUseCase = DependencyProvider.setThemeSettingsUseCase
) : AndroidViewModel(application) {

    private val context: Context = application.applicationContext

    private val _uiState = MutableStateFlow(MainScreenState())
    val uiState: StateFlow<MainScreenState> = _uiState.asStateFlow()

    private val searchQueryFlow = MutableStateFlow("")

    val themeSettings: StateFlow<ThemeSettings> = getThemeSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeSettings("SYSTEM", false)
        )

    init {
        // Observe installed apps and combine with search
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getInstalledAppsUseCase().collect { apps ->
                _uiState.update {
                    it.copy(
                        installedApps = apps,
                        isLoading = false
                    )
                }
                filterApps(searchQueryFlow.value)
            }
        }

        // Observe focus session updates reactively
        viewModelScope.launch {
            var wasSessionActiveLastCheck = false
            getFocusSessionUseCase().collect { focusSession ->
                val isActiveNow = focusSession.isActive
                val sessionEndTime = focusSession.endTime

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
                        isSessionActive = isActiveNow,
                        sessionEndTime = sessionEndTime,
                        sessionTotalDuration = focusSession.totalDuration,
                        showExtensionPrompt = showPrompt
                    )
                }
            }
        }

        // Periodic permission status monitoring
        viewModelScope.launch {
            while (true) {
                val isPermissionGranted = checkUsageStatsPermissionUseCase()
                if (_uiState.value.isUsageStatsGranted != isPermissionGranted) {
                    _uiState.update { it.copy(isUsageStatsGranted = isPermissionGranted) }
                }
                delay(1000L)
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchQueryFlow.value = query
        _uiState.update { it.copy(searchQuery = query) }
        filterApps(query)
    }

    private fun filterApps(query: String) {
        val apps = _uiState.value.installedApps
        val filtered = if (query.isEmpty()) {
            apps
        } else {
            apps.filter { it.name.contains(query, ignoreCase = true) || it.packageName.contains(query, ignoreCase = true) }
        }
        _uiState.update { it.copy(filteredApps = filtered) }
    }

    fun toggleAppBlock(packageName: String) {
        viewModelScope.launch {
            toggleBlockedAppUseCase(packageName)
        }
    }

    fun startFocusSession(durationMillis: Long) {
        viewModelScope.launch {
            startFocusSessionUseCase(durationMillis)
            startService()
        }
    }

    fun extendFocusSession(durationMillis: Long) {
        viewModelScope.launch {
            extendFocusSessionUseCase(durationMillis)
            startService()
        }
    }

    fun endFocusSession() {
        viewModelScope.launch {
            endFocusSessionUseCase()
            stopService()
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

    fun openUsageStatsSettings() {
        openUsageStatsSettingsUseCase()
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            setThemeSettingsUseCase.setThemeMode(mode)
        }
    }

    fun setUseDynamicColor(use: Boolean) {
        viewModelScope.launch {
            setThemeSettingsUseCase.setUseDynamicColor(use)
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
}
