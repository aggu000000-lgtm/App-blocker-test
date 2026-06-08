package com.antigravity.distractionshield.di

import android.content.Context
import com.antigravity.distractionshield.data.datasource.PreferencesDataSource
import com.antigravity.distractionshield.data.repository.AppRepositoryImpl
import com.antigravity.distractionshield.data.repository.BlockedAppsRepositoryImpl
import com.antigravity.distractionshield.data.repository.SessionRepositoryImpl
import com.antigravity.distractionshield.data.repository.ThemeRepositoryImpl
import com.antigravity.distractionshield.data.repository.StatsRepositoryImpl
import com.antigravity.distractionshield.domain.repository.AppRepository
import com.antigravity.distractionshield.domain.repository.BlockedAppsRepository
import com.antigravity.distractionshield.domain.repository.SessionRepository
import com.antigravity.distractionshield.domain.repository.ThemeRepository
import com.antigravity.distractionshield.domain.repository.StatsRepository
import com.antigravity.distractionshield.domain.usecase.CheckUsageStatsPermissionUseCase
import com.antigravity.distractionshield.domain.usecase.EndFocusSessionUseCase
import com.antigravity.distractionshield.domain.usecase.ExtendFocusSessionUseCase
import com.antigravity.distractionshield.domain.usecase.GetBlockedAppsUseCase
import com.antigravity.distractionshield.domain.usecase.GetFocusSessionUseCase
import com.antigravity.distractionshield.domain.usecase.GetInstalledAppsUseCase
import com.antigravity.distractionshield.domain.usecase.GetThemeSettingsUseCase
import com.antigravity.distractionshield.domain.usecase.OpenUsageStatsSettingsUseCase
import com.antigravity.distractionshield.domain.usecase.StartFocusSessionUseCase
import com.antigravity.distractionshield.domain.usecase.SetThemeSettingsUseCase
import com.antigravity.distractionshield.domain.usecase.ToggleBlockedAppUseCase

object DependencyProvider {
    private lateinit var appContext: Context

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    private val preferencesDataSource: PreferencesDataSource by lazy {
        PreferencesDataSource(appContext)
    }

    val themeRepository: ThemeRepository by lazy {
        ThemeRepositoryImpl(preferencesDataSource)
    }

    val blockedAppsRepository: BlockedAppsRepository by lazy {
        BlockedAppsRepositoryImpl(preferencesDataSource)
    }

    val sessionRepository: SessionRepository by lazy {
        SessionRepositoryImpl(preferencesDataSource)
    }

    val statsRepository: StatsRepository by lazy {
        StatsRepositoryImpl(preferencesDataSource)
    }

    val appRepository: AppRepository by lazy {
        AppRepositoryImpl(appContext, appContext.packageManager)
    }

    // Use Cases
    val getInstalledAppsUseCase: GetInstalledAppsUseCase by lazy {
        GetInstalledAppsUseCase(appRepository, blockedAppsRepository)
    }

    val toggleBlockedAppUseCase: ToggleBlockedAppUseCase by lazy {
        ToggleBlockedAppUseCase(blockedAppsRepository)
    }

    val getBlockedAppsUseCase: GetBlockedAppsUseCase by lazy {
        GetBlockedAppsUseCase(blockedAppsRepository)
    }

    val startFocusSessionUseCase: StartFocusSessionUseCase by lazy {
        StartFocusSessionUseCase(sessionRepository)
    }

    val extendFocusSessionUseCase: ExtendFocusSessionUseCase by lazy {
        ExtendFocusSessionUseCase(sessionRepository)
    }

    val endFocusSessionUseCase: EndFocusSessionUseCase by lazy {
        EndFocusSessionUseCase(sessionRepository)
    }

    val getFocusSessionUseCase: GetFocusSessionUseCase by lazy {
        GetFocusSessionUseCase(sessionRepository)
    }

    val getThemeSettingsUseCase: GetThemeSettingsUseCase by lazy {
        GetThemeSettingsUseCase(themeRepository)
    }

    val setThemeSettingsUseCase: SetThemeSettingsUseCase by lazy {
        SetThemeSettingsUseCase(themeRepository)
    }

    val checkUsageStatsPermissionUseCase: CheckUsageStatsPermissionUseCase by lazy {
        CheckUsageStatsPermissionUseCase(appRepository)
    }

    val openUsageStatsSettingsUseCase: OpenUsageStatsSettingsUseCase by lazy {
        OpenUsageStatsSettingsUseCase(appRepository)
    }
}
