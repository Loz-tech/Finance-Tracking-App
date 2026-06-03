package com.financetracker.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.financetracker.R
import com.financetracker.data.export.CsvExporter
import com.financetracker.data.export.JsonExporter
import com.financetracker.data.local.prefs.UserPreferences
import com.financetracker.domain.repository.CategoryRepository
import com.financetracker.domain.repository.SettingsRepository
import com.financetracker.domain.repository.TransactionRepository
import com.financetracker.util.LocaleHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class SettingsUiState(
    val themeMode: Int = UserPreferences.THEME_LIGHT,
    val accentColorIndex: Int = 0,
    val iconStyle: Int = UserPreferences.ICON_STYLE_FILLED,
    val languageTag: String = "",
    val message: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    @ApplicationContext private val appContext: Context,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val csvExporter: CsvExporter,
    private val jsonExporter: JsonExporter
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        viewModelScope.launch {
            settingsRepository.userPreferences.collect { prefs ->
                _uiState.value = _uiState.value.copy(
                    themeMode = prefs.themeMode,
                    accentColorIndex = prefs.accentColorIndex,
                    iconStyle = prefs.iconStyle,
                    languageTag = prefs.languageTag
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

    fun setIconStyle(style: Int) {
        viewModelScope.launch {
            settingsRepository.setIconStyle(style)
        }
    }

    fun setLanguage(tag: String) {
        viewModelScope.launch {
            settingsRepository.setLanguage(tag)
            LocaleHelper.setAppLocale(appContext, tag)
            _uiState.value = _uiState.value.copy(languageTag = tag)
        }
    }

    fun exportCsv() {
        viewModelScope.launch {
            val transactions = transactionRepository.getAllTransactions().first()
            val file = csvExporter.export(transactions)
            _uiState.value = _uiState.value.copy(
                message = appContext.getString(R.string.msg_csv_exported, file.absolutePath)
            )
        }
    }

    fun exportJson() {
        viewModelScope.launch {
            val transactions = transactionRepository.getAllTransactions().first()
            val file = jsonExporter.export(transactions)
            _uiState.value = _uiState.value.copy(
                message = appContext.getString(R.string.msg_json_exported, file.absolutePath)
            )
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            transactionRepository.deleteAllTransactions()
            categoryRepository.seedDefaultCategories()
            _uiState.value = _uiState.value.copy(
                message = appContext.getString(R.string.msg_reset_complete)
            )
        }
    }
}
