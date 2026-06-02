package com.financetracker.data.local.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

data class UserPreferences(
    val themeMode: Int = THEME_LIGHT,
    val accentColorIndex: Int = 0,
    val isBatterySaver: Boolean = false,
    val iconStyle: Int = ICON_STYLE_FILLED,
    val languageTag: String = ""
) {
    companion object {
        const val THEME_LIGHT = 0
        const val THEME_DARK = 1
        const val THEME_SYSTEM = 2

        const val ICON_STYLE_FILLED = 0
        const val ICON_STYLE_OUTLINED = 1
        const val ICON_STYLE_ROUNDED = 2
    }
}

@Singleton
class SettingsDataStore @Inject constructor(@ApplicationContext private val context: Context) {
    companion object {
        private val KEY_THEME_MODE = intPreferencesKey("theme_mode")
        private val KEY_ACCENT_COLOR = intPreferencesKey("accent_color")
        private val KEY_ICON_STYLE = intPreferencesKey("icon_style")
        private val KEY_LANGUAGE = stringPreferencesKey("language")
    }

    val userPreferences: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            themeMode = prefs[KEY_THEME_MODE] ?: UserPreferences.THEME_LIGHT,
            accentColorIndex = prefs[KEY_ACCENT_COLOR] ?: 0,
            iconStyle = prefs[KEY_ICON_STYLE] ?: UserPreferences.ICON_STYLE_FILLED,
            languageTag = prefs[KEY_LANGUAGE] ?: ""
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

    suspend fun setIconStyle(style: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ICON_STYLE] = style
        }
    }

    suspend fun setLanguage(tag: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LANGUAGE] = tag
        }
    }
}
