package com.financetracker.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.financetracker.domain.model.Period
import com.financetracker.domain.repository.TransactionRepository
import com.financetracker.ui.components.DonutSegment
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
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
class AnalyticsViewModel @Inject constructor(private val transactionRepository: TransactionRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState

    init {
        loadData(Period.MONTH)
    }

    fun onPeriodSelected(period: Period) {
        _uiState.value = _uiState.value.copy(selectedPeriod = period)
        loadData(period)
    }

    private fun loadData(period: Period) {
        viewModelScope.launch {
            val today = LocalDate.now()
            val (start, end) = when (period) {
                Period.WEEK -> today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)) to today
                Period.MONTH -> today.withDayOfMonth(1) to today.withDayOfMonth(today.lengthOfMonth())
                Period.YEAR -> today.withDayOfYear(1) to today.withDayOfYear(today.lengthOfYear())
            }

            val transactions = transactionRepository.getTransactionsByDateRange(start, end).first()
            val totalSpent = transactions.fold(BigDecimal.ZERO) { acc, t -> acc + t.amount }
            val daysSpan = if (end > start) (end.toEpochDay() - start.toEpochDay()).toInt() + 1 else 1
            val dailyAverage = totalSpent / BigDecimal(daysSpan)

            // Category breakdown
            val chartColors = listOf(
                0xFF006874,
                0xFF496364,
                0xFF634186,
                0xFFBA1A1A,
                0xFF8B4A00,
                0xFF006E28,
                0xFF90416A,
                0xFF005CBB
            )
                .map { androidx.compose.ui.graphics.Color(it) }
            val categoryGroups = transactions.groupBy { it.category }
            val categorySegments = categoryGroups.entries.mapIndexed { i, (cat, txns) ->
                DonutSegment(
                    cat.name,
                    cat.emoji,
                    txns.sumOf { it.amount.toDouble() }.toFloat(),
                    chartColors[
                        i %
                            chartColors.size
                    ]
                )
            }

            // Weekday averages
            val totalDays = maxOf(1, (end.toEpochDay() - start.toEpochDay()).toInt() + 1)
            val weekdayData = (0..6).map { dayOfWeek ->
                val dayTotal = transactions.filter { it.date.dayOfWeek.value == (dayOfWeek % 7) + 1 }
                    .sumOf { it.amount.toDouble() }
                val count = (start.toEpochDay()..end.toEpochDay()).count {
                    LocalDate.ofEpochDay(it).dayOfWeek.value == (dayOfWeek % 7) + 1
                }
                WeekdayBar(
                    DayOfWeek.of((dayOfWeek % 7) + 1).name.take(3),
                    if (count > 0) dayTotal / count else 0.0,
                    emptyList()
                )
            }

            _uiState.value = AnalyticsUiState(
                selectedPeriod = period,
                totalSpent = totalSpent,
                dailyAverage = dailyAverage,
                transactionCount = transactions.size,
                categorySegments = categorySegments,
                weekdayBars = weekdayData,
                isLoading = false
            )
        }
    }
}
