package com.financetracker.ui.history

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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DateGroup(val date: LocalDate, val label: String, val transactions: List<Transaction>)

data class HistoryUiState(
    val dateGroups: List<DateGroup> = emptyList(),
    val currentYearMonth: YearMonth = YearMonth.now(),
    val isLoading: Boolean = true
)

@HiltViewModel
class HistoryViewModel @Inject constructor(private val transactionRepository: TransactionRepository) : ViewModel() {

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
                        date == today -> "Today"
                        date == today.minusDays(1) -> "Yesterday"
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
            }.stateIn(viewModelScope)
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
