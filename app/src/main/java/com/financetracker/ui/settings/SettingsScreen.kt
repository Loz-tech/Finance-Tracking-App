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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
        SettingsCard(title = "Theme") {
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
                        UserPreferences.THEME_LIGHT -> "☀ Light"
                        UserPreferences.THEME_DARK -> "🌙 Dark"
                        else -> "🔘 System"
                    }
                }
            )
        }

        // Accent color picker
        SettingsCard(title = "Accent Color") {
            AccentColorPicker(
                selectedIndex = uiState.accentColorIndex,
                onSelect = { viewModel.setAccentColor(it) }
            )
        }

        // History
        SettingsCard(title = "History") {
            Button(
                onClick = onNavigateToHistory,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("📜 View History")
            }
        }

        // Budget
        SettingsCard(title = "Budget") {
            Button(
                onClick = onNavigateToBudget,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("📊 Manage Budgets")
            }
        }

        // Export
        SettingsCard(title = "Export Data") {
            Button(
                onClick = { viewModel.exportCsv() },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("📄 Export as CSV")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { viewModel.exportJson() },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("📋 Export as JSON")
            }
        }

        // Reset
        SettingsCard(title = "Danger Zone") {
            Button(
                onClick = { showResetDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) { Text("Reset All Data") }
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
