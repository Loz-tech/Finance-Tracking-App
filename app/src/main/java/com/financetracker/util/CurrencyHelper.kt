package com.financetracker.util

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

fun Context.currentLocale(): Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    resources.configuration.locales.get(0)
} else {
    @Suppress("DEPRECATION")
    resources.configuration.locale
}

@Composable
fun rememberCurrencySymbol(): String {
    val context = LocalContext.current
    return remember {
        val locale = context.currentLocale()
        try {
            Currency.getInstance(locale).symbol
        } catch (_: IllegalArgumentException) {
            "\u0024"
        }
    }
}

@Composable
fun rememberCurrencyFormatter(): NumberFormat {
    val context = LocalContext.current
    return remember {
        NumberFormat.getCurrencyInstance(context.currentLocale())
    }
}
