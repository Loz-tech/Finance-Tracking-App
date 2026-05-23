package com.financetracker.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import androidx.compose.ui.graphics.Color
import com.financetracker.R
import com.financetracker.domain.model.CategoryIcons
import com.financetracker.domain.model.IconStyle

class TransactionWidgetReceiver : AppWidgetProvider() {

    companion object {
        private const val TAG = "TransactionWidget"

        fun refreshAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, TransactionWidgetReceiver::class.java)
            val widgetIds = appWidgetManager.getAppWidgetIds(componentName)
            Log.d(TAG, "refreshAllWidgets: widgetIds=${widgetIds.toList()}")
            if (widgetIds.isNotEmpty()) {
                val intent = Intent(context, TransactionWidgetReceiver::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
                }
                context.sendBroadcast(intent)
            }
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Log.d(TAG, "onUpdate: appWidgetIds=${appWidgetIds.toList()}")
        try {
            val store = WidgetCategoryStore(context.applicationContext)
            val categories = store.getCategories()
            Log.d(TAG, "onUpdate: categories count=${categories.size}")

            appWidgetIds.forEach { appWidgetId ->
                updateWidget(context, appWidgetManager, appWidgetId, categories)
            }
            Log.d(TAG, "onUpdate: all widgets updated successfully")
        } catch (e: Exception) {
            Log.e(TAG, "onUpdate failed", e)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        Log.d(TAG, "onAppWidgetOptionsChanged: appWidgetId=$appWidgetId")
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

        val iconSlotWidth = 68
        val rowHeight = 68
        val padding = 8

        val iconsPerRow = ((minWidth - padding) / iconSlotWidth).coerceAtLeast(1)
        val maxRows = ((minHeight - padding) / rowHeight).coerceAtLeast(1)
        val totalCapacity = (iconsPerRow * maxRows).coerceAtMost(8)

        val views = RemoteViews(context.packageName, R.layout.widget_transaction)

        if (categories.isEmpty()) {
            views.setViewVisibility(R.id.widget_placeholder, android.view.View.VISIBLE)
            views.setViewVisibility(R.id.widget_container, android.view.View.GONE)
            views.setTextViewText(R.id.widget_placeholder, "\u2699\uFE0F")
        } else {
            views.setViewVisibility(R.id.widget_placeholder, android.view.View.GONE)
            views.setViewVisibility(R.id.widget_container, android.view.View.VISIBLE)

            views.removeAllViews(R.id.widget_container)

            val displayCategories = categories.take(totalCapacity)
            val rows = displayCategories.chunked(iconsPerRow)

            rows.forEach { rowCategories ->
                val rowView = RemoteViews(context.packageName, R.layout.widget_row)
                rowCategories.forEach { category ->
                    val itemView = RemoteViews(context.packageName, R.layout.widget_category_item)

                    try {
                        val iconVector = CategoryIcons.resolve(category.iconName, IconStyle.FILLED)
                        val iconSizePx = (24 * context.resources.displayMetrics.density).toInt()
                        val tintColor = WidgetColorScheme.widgetIconTint(context)
                        val iconBitmap = WidgetIconRenderer.render(
                            context,
                            iconVector,
                            iconSizePx,
                            Color(tintColor)
                        )
                        itemView.setImageViewBitmap(R.id.category_icon, iconBitmap)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to render icon for ${category.iconName}", e)
                    }

                    val intent = Intent(context, QuickAddTransactionActivity::class.java).apply {
                        putExtra("categoryId", category.id)
                        putExtra("categoryName", category.name)
                        putExtra("categoryIconName", category.iconName)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }

                    val pendingIntent = PendingIntent.getActivity(
                        context,
                        category.id.hashCode(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    itemView.setOnClickPendingIntent(R.id.category_icon, pendingIntent)
                    rowView.addView(R.id.row_container, itemView)
                }
                views.addView(R.id.widget_container, rowView)
            }
        }

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
