package com.financetracker.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.financetracker.domain.model.Period
import com.financetracker.domain.usecase.GetAnalyticsDataUseCase
import com.financetracker.ui.components.charts.DonutSegment
import com.financetracker.ui.state.UiStateHolder
import com.financetracker.ui.theme.ChartColors
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import java.time.DayOfWeek
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

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

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AnalyticsViewModel @Inject constructor(private val getAnalyticsDataUseCase: GetAnalyticsDataUseCase) :
    ViewModel() {

    private val periodFlow = MutableStateFlow(Period.MONTH)
    private val stateHolder = UiStateHolder(AnalyticsUiState(isLoading = true), viewModelScope)
    val uiState = stateHolder.state

    init {
        stateHolder.load(
            periodFlow.flatMapLatest { period ->
                getAnalyticsDataUseCase(period).map { data ->
                    AnalyticsUiState(
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
        )
    }

    fun onPeriodSelected(period: Period) {
        periodFlow.value = period
    }
}
