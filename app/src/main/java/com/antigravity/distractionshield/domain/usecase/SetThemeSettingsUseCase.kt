package com.antigravity.distractionshield.domain.usecase

import com.antigravity.distractionshield.domain.repository.ThemeRepository

class SetThemeSettingsUseCase(
    private val themeRepository: ThemeRepository
) {
    suspend fun setThemeMode(mode: String) {
        themeRepository.setThemeMode(mode)
    }

    suspend fun setUseDynamicColor(use: Boolean) {
        themeRepository.setUseDynamicColor(use)
    }
}
