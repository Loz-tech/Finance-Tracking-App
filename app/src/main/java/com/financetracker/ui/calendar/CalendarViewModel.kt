package com.financetracker.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.financetracker.domain.model.Transaction
import com.financetracker.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CalendarDay(
    val date: LocalDate,
    val total: Double,
    val transactions: List<Transaction>,
    // 0-4
    val intensity: Int
)

data class CalendarUiState(
    val yearMonth: YearMonth = YearMonth.now(),
    val days: List<CalendarDay> = emptyList(),
    val selectedDay: CalendarDay? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class CalendarViewModel @Inject constructor(private val transactionRepository: TransactionRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState

    init {
        loadMonth(YearMonth.now())
    }

    fun previousMonth() {
        loadMonth(_uiState.value.yearMonth.minusMonths(1))
    }
    fun nextMonth() {
        loadMonth(_uiState.value.yearMonth.plusMonths(1))
    }

    fun onDayClicked(day: CalendarDay) {
        _uiState.value = if (_uiState.value.selectedDay?.date == day.date) {
            _uiState.value.copy(selectedDay = null)
        } else {
            _uiState.value.copy(selectedDay = day)
        }
    }

    private fun loadMonth(yearMonth: YearMonth) {
        viewModelScope.launch {
            val start = yearMonth.atDay(1)
            val end = yearMonth.atEndOfMonth()
            val today = LocalDate.now()
            val dailyTotals = transactionRepository.getDailyTotals(start, end)

            val monthMax = dailyTotals.values.maxOrNull() ?: 1.0

            val allDays = start.datesUntil(end.plusDays(1)).toList()
            val days = allDays.map { date ->
                val total = dailyTotals[date] ?: 0.0
                val intensity = if (monthMax > 0) ((total / monthMax) * 4).toInt().coerceIn(0, 4) else 0
                CalendarDay(date, total, emptyList(), intensity)
            }

            _uiState.value = CalendarUiState(yearMonth, days, null, false)
        }
    }
}
