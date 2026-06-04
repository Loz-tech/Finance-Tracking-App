package com.financetracker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.financetracker.data.local.prefs.UserPreferences
import com.financetracker.domain.model.ExportFormat
import com.financetracker.domain.repository.ExchangeRateRepository
import com.financetracker.domain.repository.SettingsRepository
import com.financetracker.domain.usecase.ChangeCurrencyUseCase
import com.financetracker.domain.usecase.ExportTransactionsUseCase
import com.financetracker.domain.usecase.ResetDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val themeMode: Int = UserPreferences.THEME_LIGHT,
    val accentColorIndex: Int = 0,
    val iconStyle: Int = UserPreferences.ICON_STYLE_FILLED,
    val languageTag: String = "",
    val currencyCode: String = "USD",
    val manualRate: String = "",
    val showManualRate: Boolean = false,
    val lastUpdated: String? = null,
    val isLoading: Boolean = false
)

sealed class SettingsEvent {
    data class CurrencyChanged(val newCode: String) : SettingsEvent()
    object CurrencyChangeFailed : SettingsEvent()
    object RatesRefreshed : SettingsEvent()
    object RatesRefreshFailed : SettingsEvent()
    data class Exported(val filePath: String, val format: ExportFormat) : SettingsEvent()
    object ExportFailed : SettingsEvent()
    object DataReset : SettingsEvent()
    object ResetFailed : SettingsEvent()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val exchangeRateRepository: ExchangeRateRepository,
    private val changeCurrencyUseCase: ChangeCurrencyUseCase,
    private val exportTransactionsUseCase: ExportTransactionsUseCase,
    private val resetDataUseCase: ResetDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    private val _events = Channel<SettingsEvent>(Channel.BUFFERED)
    val events: Flow<SettingsEvent> = _events.receiveAsFlow()

    private var currencyChangeJob: Job? = null

    init {
        viewModelScope.launch {
            settingsRepository.userPreferences.collect { prefs ->
                _uiState.value = _uiState.value.copy(
                    themeMode = prefs.themeMode,
                    accentColorIndex = prefs.accentColorIndex,
                    iconStyle = prefs.iconStyle,
                    languageTag = prefs.languageTag,
                    currencyCode = prefs.currencyCode
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
        }
    }

    fun setShowManualRate(show: Boolean) {
        _uiState.value = _uiState.value.copy(showManualRate = show)
    }

    fun setManualRate(rate: String) {
        _uiState.value = _uiState.value.copy(manualRate = rate)
    }

    fun setCurrencyCode(code: String) {
        currencyChangeJob?.cancel()
        currencyChangeJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val current = _uiState.value.currencyCode
            val manualRateText = _uiState.value.manualRate
            val useManual = _uiState.value.showManualRate

            val result = changeCurrencyUseCase(current, code, manualRateText, useManual)

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    currencyCode = code,
                    manualRate = "",
                    showManualRate = false,
                    isLoading = false
                )
                _events.send(SettingsEvent.CurrencyChanged(code))
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _events.send(SettingsEvent.CurrencyChangeFailed)
            }
        }
    }

    fun refreshRates() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = exchangeRateRepository.refreshRates(_uiState.value.currencyCode)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    lastUpdated = Instant.now().toString()
                )
                _events.send(SettingsEvent.RatesRefreshed)
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _events.send(SettingsEvent.RatesRefreshFailed)
            }
        }
    }

    fun exportCsv() = export(ExportFormat.CSV)

    fun exportJson() = export(ExportFormat.JSON)

    private fun export(format: ExportFormat) {
        viewModelScope.launch {
            val result = exportTransactionsUseCase(format)
            if (result.isSuccess) {
                _events.send(SettingsEvent.Exported(result.getOrThrow().absolutePath, format))
            } else {
                _events.send(SettingsEvent.ExportFailed)
            }
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            val result = resetDataUseCase()
            if (result.isSuccess) {
                _events.send(SettingsEvent.DataReset)
            } else {
                _events.send(SettingsEvent.ResetFailed)
            }
        }
    }
}
