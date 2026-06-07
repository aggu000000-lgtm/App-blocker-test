package com.antigravity.distractionshield.domain.usecase

import com.antigravity.distractionshield.domain.model.ThemeSettings
import com.antigravity.distractionshield.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow

class GetThemeSettingsUseCase(
    private val themeRepository: ThemeRepository
) {
    operator fun invoke(): Flow<ThemeSettings> {
        return themeRepository.themeSettings
    }
}
