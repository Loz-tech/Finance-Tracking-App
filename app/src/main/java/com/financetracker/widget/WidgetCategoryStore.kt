package com.financetracker.widget

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.financetracker.domain.model.Category
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Singleton
class WidgetCategoryStore @Inject constructor(
    @param:dagger.hilt.android.qualifiers.ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveCategories(categories: List<Category>) {
        val widgetCategories = categories.map {
            WidgetCategory(id = it.id.toString(), name = it.name, iconName = it.iconName)
        }
        val json = Json.encodeToString(widgetCategories)
        prefs.edit { putString(KEY_CATEGORIES, json) }
        updateWidgets()
    }

    fun getCategories(): List<WidgetCategory> {
        val json = prefs.getString(KEY_CATEGORIES, null) ?: return emptyList()
        return try {
            Json.decodeFromString<List<WidgetCategory>>(json)
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun updateWidgets() {
        TransactionWidgetReceiver.refreshAllWidgets(context)
    }

    companion object {
        private const val PREFS_NAME = "widget_categories"
        private const val KEY_CATEGORIES = "categories"
    }
}
