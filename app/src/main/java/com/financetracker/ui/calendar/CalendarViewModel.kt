package com.financetracker.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.financetracker.domain.model.Transaction
import com.financetracker.domain.usecase.GetCalendarMonthDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.Job
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
class CalendarViewModel @Inject constructor(private val getCalendarMonthDataUseCase: GetCalendarMonthDataUseCase) :
    ViewModel() {

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

    private var loadMonthJob: Job? = null

    private fun loadMonth(yearMonth: YearMonth) {
        loadMonthJob?.cancel()
        loadMonthJob = viewModelScope.launch {
            val data = getCalendarMonthDataUseCase(yearMonth)
            val start = yearMonth.atDay(1)
            val end = yearMonth.atEndOfMonth()
            val allDays = start.datesUntil(end.plusDays(1)).toList()
            val days = allDays.map { date ->
                val total = data.dayTotals[date] ?: 0.0
                val intensity = if (data.monthMax > 0) ((total / data.monthMax) * 4).toInt().coerceIn(0, 4) else 0
                val dayTransactions = data.transactionsByDate[date] ?: emptyList()
                CalendarDay(date, total, dayTransactions, intensity)
            }

            _uiState.value = CalendarUiState(yearMonth, days, null, false)
        }
    }
}
