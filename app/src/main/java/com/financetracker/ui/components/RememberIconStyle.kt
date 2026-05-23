package com.financetracker.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.financetracker.domain.model.IconStyle
import com.financetracker.ui.settings.SettingsViewModel

@Composable
fun rememberIconStyle(): IconStyle {
    val vm: SettingsViewModel = hiltViewModel()
    val uiState by vm.uiState.collectAsState()
    return remember(uiState.iconStyle) {
        IconStyle.entries[uiState.iconStyle.coerceIn(0, IconStyle.entries.size - 1)]
    }
}
