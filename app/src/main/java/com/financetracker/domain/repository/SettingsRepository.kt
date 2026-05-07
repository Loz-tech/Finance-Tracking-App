package com.financetracker.domain.repository

import com.financetracker.data.local.prefs.UserPreferences
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val userPreferences: Flow<UserPreferences>
    suspend fun setThemeMode(mode: Int)
    suspend fun setAccentColor(index: Int)
}
