package com.antigravity.distractionshield.data.repository

import com.antigravity.distractionshield.data.datasource.PreferencesDataSource
import com.antigravity.distractionshield.domain.model.ThemeSettings
import com.antigravity.distractionshield.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class ThemeRepositoryImpl(
    private val dataSource: PreferencesDataSource
) : ThemeRepository {
    override val themeSettings: Flow<ThemeSettings> = dataSource.preferenceChangesFlow
        .filter { it == PreferencesDataSource.KEY_THEME_MODE || it == PreferencesDataSource.KEY_USE_DYNAMIC_COLOR }
        .onStart { emit("") }
        .map {
            ThemeSettings(
                themeMode = dataSource.getThemeMode(),
                useDynamicColor = dataSource.getUseDynamicColor()
            )
        }
        .distinctUntilChanged()

    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(
            themeMode = dataSource.getThemeMode(),
            useDynamicColor = dataSource.getUseDynamicColor()
        )
    }

    override suspend fun setThemeMode(mode: String) {
        dataSource.setThemeMode(mode)
    }

    override suspend fun setUseDynamicColor(use: Boolean) {
        dataSource.setUseDynamicColor(use)
    }
}
