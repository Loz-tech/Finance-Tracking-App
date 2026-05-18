package com.financetracker.data.local.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

data class UserPreferences(
    val themeMode: Int = THEME_LIGHT,
    val accentColorIndex: Int = 0,
    val isBatterySaver: Boolean = false
) {
    companion object {
        const val THEME_LIGHT = 0
        const val THEME_DARK = 1
        const val THEME_OLED = 2
    }
}

@Singleton
class SettingsDataStore @Inject constructor(@ApplicationContext private val context: Context) {
    companion object {
        private val KEY_THEME_MODE = intPreferencesKey("theme_mode")
        private val KEY_ACCENT_COLOR = intPreferencesKey("accent_color")
    }

    val userPreferences: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            themeMode = prefs[KEY_THEME_MODE] ?: UserPreferences.THEME_LIGHT,
            accentColorIndex = prefs[KEY_ACCENT_COLOR] ?: 0
        )
    }

    suspend fun setThemeMode(mode: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_THEME_MODE] = mode
        }
    }

    suspend fun setAccentColor(index: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ACCENT_COLOR] = index
        }
    }
}
