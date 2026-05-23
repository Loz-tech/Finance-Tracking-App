package com.financetracker.widget

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import com.financetracker.data.local.prefs.UserPreferences
import com.financetracker.data.local.prefs.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object WidgetSettingsReader {
    private val KEY_ICON_STYLE = intPreferencesKey("icon_style")

    fun readIconStyle(context: Context): Int = runBlocking {
        context.dataStore.data.first()[KEY_ICON_STYLE] ?: UserPreferences.ICON_STYLE_FILLED
    }
}
