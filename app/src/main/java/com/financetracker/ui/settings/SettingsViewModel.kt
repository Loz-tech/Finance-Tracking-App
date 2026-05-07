package com.financetracker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.financetracker.data.local.prefs.UserPreferences
import com.financetracker.domain.repository.CategoryRepository
import com.financetracker.domain.repository.SettingsRepository
import com.financetracker.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val themeMode: Int = UserPreferences.THEME_LIGHT,
    val accentColorIndex: Int = 0,
    val message: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        viewModelScope.launch {
            settingsRepository.userPreferences.collect { prefs ->
                _uiState.value = _uiState.value.copy(
                    themeMode = prefs.themeMode,
                    accentColorIndex = prefs.accentColorIndex
                )
            }
        }
    }

    fun setThemeMode(mode: Int) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(mode)
        }
    }

    fun setAccentColor(index: Int) {
        viewModelScope.launch {
            settingsRepository.setAccentColor(index)
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            transactionRepository.deleteAllTransactions()
            categoryRepository.seedDefaultCategories()
            _uiState.value = _uiState.value.copy(message = "All data has been reset")
        }
    }
}
