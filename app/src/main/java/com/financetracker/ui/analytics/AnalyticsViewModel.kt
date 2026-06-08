package com.financetracker.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.financetracker.domain.model.Period
import com.financetracker.domain.usecase.GetAnalyticsDataUseCase
import com.financetracker.ui.components.charts.DonutSegment
import com.financetracker.ui.theme.ChartColors
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import java.time.DayOfWeek
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class WeekdayBar(val day: String, val average: Double, val raw: List<Double>)

data class AnalyticsUiState(
    val selectedPeriod: Period = Period.MONTH,
    val totalSpent: BigDecimal = BigDecimal.ZERO,
    val dailyAverage: BigDecimal = BigDecimal.ZERO,
    val transactionCount: Int = 0,
    val categorySegments: List<DonutSegment> = emptyList(),
    val weekdayBars: List<WeekdayBar> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(private val getAnalyticsDataUseCase: GetAnalyticsDataUseCase) :
    ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState
    private var loadDataJob: Job? = null

    init {
        loadData(Period.MONTH)
    }

    fun onPeriodSelected(period: Period) {
        _uiState.value = _uiState.value.copy(selectedPeriod = period)
        loadData(period)
    }

    private fun loadData(period: Period) {
        loadDataJob?.cancel()
        loadDataJob = viewModelScope.launch {
            getAnalyticsDataUseCase(period).collect { data ->
                _uiState.value = AnalyticsUiState(
                    selectedPeriod = period,
                    totalSpent = data.totalSpent,
                    dailyAverage = data.dailyAverage,
                    transactionCount = data.transactionCount,
                    categorySegments = data.categoryBreakdowns.mapIndexed { i, cb ->
                        DonutSegment(
                            cb.name,
                            cb.iconName,
                            cb.amount.toFloat(),
                            ChartColors[i % ChartColors.size]
                        )
                    },
                    weekdayBars = DayOfWeek.entries.map { day ->
                        WeekdayBar(
                            day.name.take(3),
                            data.weekdayAverages[day] ?: 0.0,
                            emptyList()
                        )
                    },
                    isLoading = false
                )
            }
        }
    }
}
