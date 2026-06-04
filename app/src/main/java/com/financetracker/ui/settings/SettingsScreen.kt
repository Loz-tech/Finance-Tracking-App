package com.financetracker.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.financetracker.R
import com.financetracker.data.local.prefs.UserPreferences
import com.financetracker.domain.model.ExportFormat
import com.financetracker.ui.components.core.SettingsCard
import com.financetracker.ui.components.input.AccentColorPicker
import com.financetracker.ui.components.input.FilterChipGroup
import com.financetracker.ui.components.input.ResetDataDialog
import com.financetracker.util.LocaleHelper

@Composable
fun SettingsScreen(
    onNavigateToBudget: () -> Unit,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }
    var showCurrencyConfirm by remember { mutableStateOf(false) }
    var pendingCurrency by remember { mutableStateOf("") }
    val context = LocalContext.current
    var message by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            message = when (event) {
                is SettingsEvent.CurrencyChanged ->
                    context.getString(R.string.msg_currency_changed, event.newCode)

                SettingsEvent.CurrencyChangeFailed ->
                    context.getString(R.string.error_currency_change)

                SettingsEvent.RatesRefreshed ->
                    context.getString(R.string.msg_rates_refreshed)

                SettingsEvent.RatesRefreshFailed ->
                    context.getString(R.string.error_rates_refresh)

                is SettingsEvent.Exported -> when (event.format) {
                    ExportFormat.CSV -> context.getString(R.string.msg_csv_exported, event.filePath)
                    ExportFormat.JSON -> context.getString(R.string.msg_json_exported, event.filePath)
                }

                SettingsEvent.ExportFailed -> context.getString(R.string.error_export_failed)

                SettingsEvent.DataReset ->
                    context.getString(R.string.msg_reset_complete)

                SettingsEvent.ResetFailed -> context.getString(R.string.error_reset_failed)
            }
        }
    }

    LaunchedEffect(message) {
        if (message != null) {
            kotlinx.coroutines.delay(3000)
            message = null
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Theme selector
        SettingsCard(title = stringResource(R.string.settings_theme)) {
            FilterChipGroup(
                items = listOf(
                    UserPreferences.THEME_LIGHT,
                    UserPreferences.THEME_DARK,
                    UserPreferences.THEME_SYSTEM
                ),
                selected = { it == uiState.themeMode },
                onSelect = { viewModel.setThemeMode(it) },
                label = {
                    when (it) {
                        UserPreferences.THEME_LIGHT -> stringResource(R.string.settings_theme_light)
                        UserPreferences.THEME_DARK -> stringResource(R.string.settings_theme_dark)
                        else -> stringResource(R.string.settings_theme_system)
                    }
                }
            )
        }

        // Accent color picker
        SettingsCard(title = stringResource(R.string.settings_accent_color)) {
            AccentColorPicker(
                selectedIndex = uiState.accentColorIndex,
                onSelect = { viewModel.setAccentColor(it) }
            )
        }

        // Language selector
        SettingsCard(title = stringResource(R.string.settings_language)) {
            val languages = listOf(
                "" to stringResource(R.string.settings_language_system),
                "en" to stringResource(R.string.language_en),
                "es" to stringResource(R.string.language_es),
                "ru" to stringResource(R.string.language_ru)
            )
            Column {
                languages.forEach { (tag, label) ->
                    val selected = uiState.languageTag == tag
                    TextButton(
                        onClick = {
                            viewModel.setLanguage(tag)
                            LocaleHelper.setAppLocale(context, tag)
                        }
                    ) {
                        Text(
                            text = if (selected) "✓ $label" else label,
                            color = if (selected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }
        }

        // Currency selector
        SettingsCard(title = stringResource(R.string.settings_currency)) {
            val currencies = listOf(
                "USD" to stringResource(R.string.settings_currency_usd),
                "EUR" to stringResource(R.string.settings_currency_eur),
                "GBP" to stringResource(R.string.settings_currency_gbp),
                "JPY" to stringResource(R.string.settings_currency_jpy),
                "CNY" to stringResource(R.string.settings_currency_cny)
            )
            Column {
                currencies.forEach { (code, label) ->
                    val selected = uiState.currencyCode == code
                    TextButton(
                        onClick = {
                            if (!selected && !uiState.isLoading) {
                                pendingCurrency = code
                                showCurrencyConfirm = true
                            }
                        }
                    ) {
                        Text(
                            text = if (selected) "✓ $label" else label,
                            color = if (selected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 8.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                Checkbox(
                    checked = uiState.showManualRate,
                    onCheckedChange = { viewModel.setShowManualRate(it) }
                )
                Text(stringResource(R.string.settings_manual_rate), style = MaterialTheme.typography.bodyMedium)
            }
            if (uiState.showManualRate) {
                OutlinedTextField(
                    value = uiState.manualRate,
                    onValueChange = { viewModel.setManualRate(it) },
                    label = { Text(stringResource(R.string.settings_manual_rate_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { viewModel.refreshRates() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(stringResource(R.string.settings_refresh_rates))
            }
            if (uiState.lastUpdated != null) {
                Text(
                    text = stringResource(R.string.settings_last_updated, uiState.lastUpdated!!),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Text(
                text = stringResource(R.string.settings_rates_disclaimer),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // History
        SettingsCard(title = stringResource(R.string.settings_history)) {
            Button(
                onClick = onNavigateToHistory,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(stringResource(R.string.settings_history_view))
            }
        }

        // Budget
        SettingsCard(title = stringResource(R.string.settings_budget)) {
            Button(
                onClick = onNavigateToBudget,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(stringResource(R.string.settings_budget_manage))
            }
        }

        // Export
        SettingsCard(title = stringResource(R.string.settings_export_data)) {
            Button(
                onClick = { viewModel.exportCsv() },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(stringResource(R.string.settings_export_csv))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { viewModel.exportJson() },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(stringResource(R.string.settings_export_json))
            }
        }

        // Reset
        SettingsCard(title = stringResource(R.string.settings_danger_zone)) {
            Button(
                onClick = { showResetDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) { Text(stringResource(R.string.settings_reset_all_data)) }
        }

        if (showCurrencyConfirm) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showCurrencyConfirm = false },
                title = { Text(stringResource(R.string.dialog_currency_change_title)) },
                text = { Text(stringResource(R.string.dialog_currency_change_body)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showCurrencyConfirm = false
                            viewModel.setCurrencyCode(pendingCurrency)
                        }
                    ) { Text(stringResource(R.string.dialog_currency_change_confirm)) }
                },
                dismissButton = {
                    TextButton(onClick = { showCurrencyConfirm = false }) {
                        Text(stringResource(R.string.dialog_cancel))
                    }
                }
            )
        }

        message?.let { msg ->
            Text(
                msg,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    if (showResetDialog) {
        ResetDataDialog(
            onDismiss = { showResetDialog = false },
            onConfirm = {
                viewModel.resetAllData()
                showResetDialog = false
            }
        )
    }
}
