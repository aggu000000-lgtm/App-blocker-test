package com.antigravity.distractionshield.presentation.blocker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antigravity.distractionshield.di.DependencyProvider
import com.antigravity.distractionshield.domain.model.FocusSession
import com.antigravity.distractionshield.domain.model.ThemeSettings
import com.antigravity.distractionshield.domain.usecase.GetFocusSessionUseCase
import com.antigravity.distractionshield.domain.usecase.GetThemeSettingsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class BlockerViewModel(
    getThemeSettingsUseCase: GetThemeSettingsUseCase = DependencyProvider.getThemeSettingsUseCase,
    getFocusSessionUseCase: GetFocusSessionUseCase = DependencyProvider.getFocusSessionUseCase
) : ViewModel() {
    val themeSettings: StateFlow<ThemeSettings> = getThemeSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeSettings("SYSTEM", false)
        )

    val focusSession: StateFlow<FocusSession> = getFocusSessionUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FocusSession(false, 0L, 0L)
        )
}
