package com.financetracker.data.repository

import com.financetracker.data.local.prefs.SettingsDataStore
import com.financetracker.data.local.prefs.UserPreferences
import com.financetracker.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : SettingsRepository {

    override val userPreferences: Flow<UserPreferences> = settingsDataStore.userPreferences

    override suspend fun setThemeMode(mode: Int) {
        settingsDataStore.setThemeMode(mode)
    }

    override suspend fun setAccentColor(index: Int) {
        settingsDataStore.setAccentColor(index)
    }
}
