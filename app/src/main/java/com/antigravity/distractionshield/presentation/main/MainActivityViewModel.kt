package com.antigravity.distractionshield.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antigravity.distractionshield.di.DependencyProvider
import com.antigravity.distractionshield.domain.model.ThemeSettings
import com.antigravity.distractionshield.domain.usecase.GetThemeSettingsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MainActivityViewModel(
    getThemeSettingsUseCase: GetThemeSettingsUseCase = DependencyProvider.getThemeSettingsUseCase
) : ViewModel() {
    val themeSettings: StateFlow<ThemeSettings> = getThemeSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeSettings("SYSTEM", false)
        )
}
