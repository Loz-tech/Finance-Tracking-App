package com.financetracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.financetracker.domain.model.Transaction
import com.financetracker.domain.repository.BudgetRepository
import com.financetracker.domain.repository.TransactionRepository
import com.financetracker.ui.components.DonutSegment
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val totalSpent: BigDecimal = BigDecimal.ZERO,
    val totalBudget: BigDecimal? = null,
    val recentTransactions: List<Transaction> = emptyList(),
    val categorySegments: List<DonutSegment> = emptyList(),
    val isLoading: Boolean = true,
    val hasTransactions: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        viewModelScope.launch {
            val now = LocalDate.now()
            val yearMonth = YearMonth.now().toString()
            val monthStart = now.withDayOfMonth(1)
            val monthEnd = now.withDayOfMonth(now.lengthOfMonth())

            combine(
                transactionRepository.getTransactionsByDateRange(monthStart, monthEnd),
                transactionRepository.getRecentTransactions(5)
            ) { monthlyTransactions, recentTransactions ->
                val totalSpent = monthlyTransactions.fold(BigDecimal.ZERO) { acc, t -> acc + t.amount }
                val totalBudget = budgetRepository.getTotalBudget(yearMonth)
                val hasTransactions = monthlyTransactions.isNotEmpty()

                // Group by category for donut
                val categoryGroups = monthlyTransactions.groupBy { it.category }
                val chartColors = listOf(
                    androidx.compose.ui.graphics.Color(0xFF006874),
                    androidx.compose.ui.graphics.Color(0xFF496364),
                    androidx.compose.ui.graphics.Color(0xFF634186),
                    androidx.compose.ui.graphics.Color(0xFFBA1A1A),
                    androidx.compose.ui.graphics.Color(0xFF8B4A00),
                    androidx.compose.ui.graphics.Color(0xFF006E28),
                    androidx.compose.ui.graphics.Color(0xFF90416A),
                    androidx.compose.ui.graphics.Color(0xFF005CBB)
                )
                val segments = categoryGroups.entries.mapIndexed { i, (cat, txns) ->
                    DonutSegment(
                        label = cat.name,
                        emoji = cat.emoji,
                        value = txns.sumOf { it.amount.toDouble() }.toFloat(),
                        color = chartColors[i % chartColors.size]
                    )
                }

                _uiState.value = HomeUiState(
                    totalSpent = totalSpent,
                    totalBudget = totalBudget?.limitAmount,
                    recentTransactions = recentTransactions,
                    categorySegments = segments,
                    isLoading = false,
                    hasTransactions = hasTransactions
                )
            }.stateIn(viewModelScope)
        }
    }
}
