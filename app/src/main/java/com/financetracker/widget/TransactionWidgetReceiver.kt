package com.financetracker.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import com.financetracker.R

class TransactionWidgetReceiver : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val store = WidgetCategoryStore(context.applicationContext)
        val categories = store.getCategories()

        appWidgetIds.forEach { appWidgetId ->
            updateWidget(context, appWidgetManager, appWidgetId, categories)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        val store = WidgetCategoryStore(context.applicationContext)
        val categories = store.getCategories()
        updateWidget(context, appWidgetManager, appWidgetId, categories)
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        categories: List<WidgetCategory>
    ) {
        val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
        val minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 64)
        val minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 68)

        // Layout constants
        val iconSlotWidth = 56 // 48dp icon + 4dp margin each side
        val rowHeight = 56 // 48dp icon + 4dp margin top/bottom
        val padding = 4

        // Calculate capacity
        val iconsPerRow = ((minWidth - padding) / iconSlotWidth).coerceAtLeast(1)
        val maxRows = ((minHeight - padding) / rowHeight).coerceAtLeast(1)
        val totalCapacity = (iconsPerRow * maxRows).coerceAtMost(8)

        val views = RemoteViews(context.packageName, R.layout.widget_transaction)

        if (categories.isEmpty()) {
            views.setViewVisibility(R.id.widget_placeholder, android.view.View.VISIBLE)
            views.setViewVisibility(R.id.widget_container, android.view.View.GONE)
            views.setTextViewText(R.id.widget_placeholder, "⚙️")
        } else {
            views.setViewVisibility(R.id.widget_placeholder, android.view.View.GONE)
            views.setViewVisibility(R.id.widget_container, android.view.View.VISIBLE)

            views.removeAllViews(R.id.widget_container)

            val displayCategories = categories.take(totalCapacity)

            // Group into rows
            val rows = displayCategories.chunked(iconsPerRow)

            rows.forEach { rowCategories ->
                val rowView = RemoteViews(context.packageName, R.layout.widget_row)
                rowCategories.forEach { category ->
                    val itemView = RemoteViews(context.packageName, R.layout.widget_category_item)
                    itemView.setTextViewText(R.id.category_button, category.emoji)

                    val intent = Intent(context, QuickAddTransactionActivity::class.java).apply {
                        putExtra("categoryId", category.id)
                        putExtra("categoryName", category.name)
                        putExtra("categoryEmoji", category.emoji)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }

                    val pendingIntent = PendingIntent.getActivity(
                        context,
                        category.id.hashCode(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    itemView.setOnClickPendingIntent(R.id.category_button, pendingIntent)
                    rowView.addView(R.id.row_container, itemView)
                }
                views.addView(R.id.widget_container, rowView)
            }
        }

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    companion object {
        fun refreshAllWidgets(context: Context) {
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
    }
}
