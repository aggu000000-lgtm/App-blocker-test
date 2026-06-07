package com.antigravity.distractionshield.domain.repository

import com.antigravity.distractionshield.domain.model.ThemeSettings
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    val themeSettings: Flow<ThemeSettings>
    fun getThemeSettings(): ThemeSettings
    suspend fun setThemeMode(mode: String)
    suspend fun setUseDynamicColor(use: Boolean)
}
