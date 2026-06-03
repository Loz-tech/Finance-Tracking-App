package com.financetracker.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.financetracker.R
import com.financetracker.data.local.prefs.UserPreferences
import com.financetracker.ui.components.core.SettingsCard
import com.financetracker.ui.components.input.AccentColorPicker
import com.financetracker.ui.components.input.FilterChipGroup
import com.financetracker.ui.components.input.ResetDataDialog

@Composable
fun SettingsScreen(
    onNavigateToBudget: () -> Unit,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }

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
                        onClick = { viewModel.setLanguage(tag) }
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

        if (uiState.message != null) {
            Text(
                uiState.message!!,
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
