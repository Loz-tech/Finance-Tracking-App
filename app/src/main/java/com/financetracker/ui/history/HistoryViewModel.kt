package com.financetracker.ui.history

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.financetracker.R
import com.financetracker.domain.model.Transaction
import com.financetracker.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class DateGroup(val date: LocalDate, val label: String, val transactions: List<Transaction>)

data class HistoryUiState(
    val dateGroups: List<DateGroup> = emptyList(),
    val currentYearMonth: YearMonth = YearMonth.now(),
    val isLoading: Boolean = true
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _currentYearMonth = MutableStateFlow(YearMonth.now())
    val currentYearMonth: StateFlow<YearMonth> = _currentYearMonth
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState

    init {
        viewModelScope.launch {
            _currentYearMonth.combine(
                transactionRepository.getAllTransactions()
            ) { yearMonth, allTransactions ->
                val monthStart = yearMonth.atDay(1)
                val monthEnd = yearMonth.atEndOfMonth()

                val monthTransactions = allTransactions
                    .filter { !it.date.isBefore(monthStart) && !it.date.isAfter(monthEnd) }
                    .sortedWith(compareByDescending<Transaction> { it.date }.thenByDescending { it.createdAt })

                val today = LocalDate.now()
                val dateGroups = monthTransactions.groupBy { it.date }.map { (date, txns) ->
                    val label = when {
                        date == today -> appContext.getString(R.string.date_today)
                        date == today.minusDays(1) -> appContext.getString(R.string.date_yesterday)
                        else -> java.time.format.DateTimeFormatter.ofPattern("EEEE, MMM d")
                            .format(date)
                    }
                    DateGroup(date, label, txns)
                }.sortedByDescending { it.date }

                HistoryUiState(
                    dateGroups = dateGroups,
                    currentYearMonth = yearMonth,
                    isLoading = false
                )
            }.collect { _uiState.value = it }
        }
    }

    fun previousMonth() {
        _currentYearMonth.value = _currentYearMonth.value.minusMonths(1)
    }

    fun nextMonth() {
        _currentYearMonth.value = _currentYearMonth.value.plusMonths(1)
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transaction)
        }
    }
}
