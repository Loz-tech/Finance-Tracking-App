package com.financetracker.util

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.stringPreferencesKey
import com.financetracker.data.local.prefs.dataStore
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import kotlinx.coroutines.flow.map

fun Context.currentLocale(): Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    resources.configuration.locales.get(0)
} else {
    @Suppress("DEPRECATION")
    resources.configuration.locale
}

@Composable
fun rememberCurrencyCode(): String {
    val context = LocalContext.current
    val key = remember { stringPreferencesKey("currency") }
    val flow = remember(context, key) {
        context.dataStore.data.map { prefs -> prefs[key] ?: "USD" }
    }
    return flow.collectAsState(initial = "USD").value
}

@Composable
fun rememberCurrencySymbol(currencyCode: String? = null): String {
    val code = currencyCode ?: rememberCurrencyCode()
    return remember(code) {
        try {
            Currency.getInstance(code).symbol
        } catch (_: IllegalArgumentException) {
            "\u0024"
        }
    }
}

@Composable
fun rememberCurrencyFormatter(currencyCode: String? = null): NumberFormat {
    val code = currencyCode ?: rememberCurrencyCode()
    return remember(code) {
        NumberFormat.getCurrencyInstance().apply {
            try {
                val currency = Currency.getInstance(code)
                this.currency = currency
                maximumFractionDigits = currency.defaultFractionDigits
            } catch (_: IllegalArgumentException) {
                maximumFractionDigits = 2
            }
        }
    }
}
