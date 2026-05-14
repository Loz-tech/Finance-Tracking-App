package com.financetracker.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.financetracker.domain.model.Category
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class WidgetCategoryStore @Inject constructor(
    @param:dagger.hilt.android.qualifiers.ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveCategories(categories: List<Category>) {
        val widgetCategories = categories.map {
            WidgetCategory(id = it.id.toString(), name = it.name, emoji = it.emoji)
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
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, TransactionWidgetReceiver::class.java)
        val widgetIds = appWidgetManager.getAppWidgetIds(componentName)
        if (widgetIds.isNotEmpty()) {
            val intent = Intent(context, TransactionWidgetReceiver::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
            }
            context.sendBroadcast(intent)
        }
    }

    companion object {
        private const val PREFS_NAME = "widget_categories"
        private const val KEY_CATEGORIES = "categories"
    }
}
