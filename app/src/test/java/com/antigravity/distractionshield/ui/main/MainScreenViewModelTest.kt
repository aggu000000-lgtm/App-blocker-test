package com.antigravity.distractionshield.ui.main

import android.app.Application
import com.antigravity.distractionshield.domain.model.AppInfo
import com.antigravity.distractionshield.domain.model.FocusSession
import com.antigravity.distractionshield.domain.model.ThemeSettings
import com.antigravity.distractionshield.domain.repository.AppRepository
import com.antigravity.distractionshield.domain.repository.BlockedAppsRepository
import com.antigravity.distractionshield.domain.repository.SessionRepository
import com.antigravity.distractionshield.domain.repository.ThemeRepository
import com.antigravity.distractionshield.domain.usecase.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainScreenViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var appRepository: FakeAppRepository
    private lateinit var blockedAppsRepository: FakeBlockedAppsRepository
    private lateinit var sessionRepository: FakeSessionRepository
    private lateinit var themeRepository: FakeThemeRepository

    private lateinit var getInstalledAppsUseCase: GetInstalledAppsUseCase
    private lateinit var toggleBlockedAppUseCase: ToggleBlockedAppUseCase
    private lateinit var startFocusSessionUseCase: StartFocusSessionUseCase
    private lateinit var extendFocusSessionUseCase: ExtendFocusSessionUseCase
    private lateinit var endFocusSessionUseCase: EndFocusSessionUseCase
    private lateinit var getFocusSessionUseCase: GetFocusSessionUseCase
    private lateinit var checkUsageStatsPermissionUseCase: CheckUsageStatsPermissionUseCase
    private lateinit var openUsageStatsSettingsUseCase: OpenUsageStatsSettingsUseCase
    private lateinit var getThemeSettingsUseCase: GetThemeSettingsUseCase
    private lateinit var setThemeSettingsUseCase: SetThemeSettingsUseCase

    private lateinit var fakeApplication: Application

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        appRepository = FakeAppRepository()
        blockedAppsRepository = FakeBlockedAppsRepository()
        sessionRepository = FakeSessionRepository()
        themeRepository = FakeThemeRepository()

        getInstalledAppsUseCase = GetInstalledAppsUseCase(appRepository, blockedAppsRepository)
        toggleBlockedAppUseCase = ToggleBlockedAppUseCase(blockedAppsRepository)
        startFocusSessionUseCase = StartFocusSessionUseCase(sessionRepository)
        extendFocusSessionUseCase = ExtendFocusSessionUseCase(sessionRepository)
        endFocusSessionUseCase = EndFocusSessionUseCase(sessionRepository)
        getFocusSessionUseCase = GetFocusSessionUseCase(sessionRepository)
        checkUsageStatsPermissionUseCase = CheckUsageStatsPermissionUseCase(appRepository)
        openUsageStatsSettingsUseCase = OpenUsageStatsSettingsUseCase(appRepository)
        getThemeSettingsUseCase = GetThemeSettingsUseCase(themeRepository)
        setThemeSettingsUseCase = SetThemeSettingsUseCase(themeRepository)

        fakeApplication = Application()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialLoadingAndLoadedApps() = runTest {
        val apps = listOf(
            AppInfo("App A", "com.a", false),
            AppInfo("App B", "com.b", false)
        )
        appRepository.apps = apps

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(2, uiState.installedApps.size)
        assertEquals("App A", uiState.installedApps[0].name)
        assertEquals("App B", uiState.installedApps[1].name)
        assertFalse(uiState.isLoading)
    }

    @Test
    fun testToggleAppBlock() = runTest {
        val apps = listOf(AppInfo("App A", "com.a", false))
        appRepository.apps = apps

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleAppBlock("com.a")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(blockedAppsRepository.getBlockedApps().contains("com.a"))
    }

    @Test
    fun testStartFocusSession() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.startFocusSession(30000L)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(sessionRepository.isSessionActive())
        assertEquals(30000L, sessionRepository.getSessionTotalDuration())
    }

    private fun createViewModel(): MainScreenViewModel {
        return MainScreenViewModel(
            application = fakeApplication,
            getInstalledAppsUseCase = getInstalledAppsUseCase,
            toggleBlockedAppUseCase = toggleBlockedAppUseCase,
            startFocusSessionUseCase = startFocusSessionUseCase,
            extendFocusSessionUseCase = extendFocusSessionUseCase,
            endFocusSessionUseCase = endFocusSessionUseCase,
            getFocusSessionUseCase = getFocusSessionUseCase,
            checkUsageStatsPermissionUseCase = checkUsageStatsPermissionUseCase,
            openUsageStatsSettingsUseCase = openUsageStatsSettingsUseCase,
            getThemeSettingsUseCase = getThemeSettingsUseCase,
            setThemeSettingsUseCase = setThemeSettingsUseCase
        )
    }
}

class FakeAppRepository : AppRepository {
    var apps = listOf<AppInfo>()
    var permissionGranted = true
    var openSettingsCalled = false

    override suspend fun getInstalledApps(): List<AppInfo> = apps
    override fun isUsageStatsPermissionGranted(): Boolean = permissionGranted
    override fun openUsageStatsSettings() { openSettingsCalled = true }
}

class FakeBlockedAppsRepository : BlockedAppsRepository {
    private val _blockedApps = MutableStateFlow<Set<String>>(emptySet())
    override val blockedApps: Flow<Set<String>> = _blockedApps

    override fun getBlockedApps(): Set<String> = _blockedApps.value

    override suspend fun addBlockedApp(packageName: String) {
        _blockedApps.value = _blockedApps.value + packageName
    }

    override suspend fun removeBlockedApp(packageName: String) {
        _blockedApps.value = _blockedApps.value - packageName
    }
}

class FakeSessionRepository : SessionRepository {
    private val _focusSession = MutableStateFlow(FocusSession(false, 0L, 0L))
    override val focusSession: Flow<FocusSession> = _focusSession

    override fun isSessionActive(): Boolean = _focusSession.value.isActive
    override fun getSessionEndTime(): Long = _focusSession.value.endTime
    override fun getSessionTotalDuration(): Long = _focusSession.value.totalDuration

    override suspend fun startSession(durationMillis: Long) {
        val endTime = System.currentTimeMillis() + durationMillis
        _focusSession.value = FocusSession(true, endTime, durationMillis)
    }

    override suspend fun extendSession(durationMillis: Long) {
        val newEndTime = _focusSession.value.endTime + durationMillis
        val newDuration = _focusSession.value.totalDuration + durationMillis
        _focusSession.value = FocusSession(true, newEndTime, newDuration)
    }

    override suspend fun endSession() {
        _focusSession.value = FocusSession(false, 0L, 0L)
    }
}

class FakeThemeRepository : ThemeRepository {
    private val _themeSettings = MutableStateFlow(ThemeSettings("SYSTEM", false))
    override val themeSettings: Flow<ThemeSettings> = _themeSettings

    override fun getThemeSettings(): ThemeSettings = _themeSettings.value

    override suspend fun setThemeMode(mode: String) {
        _themeSettings.value = _themeSettings.value.copy(themeMode = mode)
    }

    override suspend fun setUseDynamicColor(use: Boolean) {
        _themeSettings.value = _themeSettings.value.copy(useDynamicColor = use)
    }
}
