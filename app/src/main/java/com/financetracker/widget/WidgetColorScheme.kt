package com.financetracker.widget

import android.content.Context
import android.content.res.Configuration
import androidx.core.content.ContextCompat
import com.financetracker.R

/**
 * Centralized color decisions for the app widget.
 * One module, two adapters (light / dark via values / values-night).
 */
object WidgetColorScheme {

    fun isSystemInDarkTheme(context: Context): Boolean = (
        context.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK
        ) == Configuration.UI_MODE_NIGHT_YES

    fun widgetBackgroundColor(context: Context): Int = ContextCompat.getColor(context, R.color.widget_bg)

    fun widgetIconBackgroundColor(context: Context): Int = ContextCompat.getColor(context, R.color.widget_icon_bg)

    fun widgetIconTint(context: Context): Int = ContextCompat.getColor(context, R.color.widget_icon_tint)
}
