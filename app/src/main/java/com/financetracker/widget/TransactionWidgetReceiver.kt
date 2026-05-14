package com.financetracker.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.financetracker.R

class TransactionWidgetReceiver : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val store = WidgetCategoryStore(context.applicationContext)
        val categories = store.getCategories()

        appWidgetIds.forEach { appWidgetId ->
            val views = RemoteViews(context.packageName, R.layout.widget_transaction)

            views.setTextViewText(R.id.widget_title, "Quick Add")

            if (categories.isEmpty()) {
                views.setViewVisibility(R.id.widget_placeholder, android.view.View.VISIBLE)
                views.setViewVisibility(R.id.widget_container, android.view.View.GONE)
            } else {
                views.setViewVisibility(R.id.widget_placeholder, android.view.View.GONE)
                views.setViewVisibility(R.id.widget_container, android.view.View.VISIBLE)

                views.removeAllViews(R.id.widget_container)

                categories.take(4).forEach { category ->
                    val itemView = RemoteViews(context.packageName, R.layout.widget_category_item)
                    itemView.setTextViewText(R.id.category_button, "${category.emoji} ${category.name}")

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
                    views.addView(R.id.widget_container, itemView)
                }
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
