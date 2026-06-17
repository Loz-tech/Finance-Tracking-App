package com.financetracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.financetracker.domain.model.CategoryBreakdown
import com.financetracker.domain.model.Transaction
import com.financetracker.domain.repository.TransactionRepository
import com.financetracker.domain.usecase.CalculateBudgetProgressUseCase
import com.financetracker.domain.usecase.DeleteTransactionUseCase
import com.financetracker.domain.usecase.GetMonthlySummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import java.time.YearMonth
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class CategoryBudgetProgress(
    val categoryId: UUID,
    val categoryName: String,
    val iconName: String,
    val colorHex: String?,
    val spent: BigDecimal,
    val limit: BigDecimal
)

data class HomeUiState(
    val totalSpent: BigDecimal = BigDecimal.ZERO,
    val totalBudget: BigDecimal? = null,
    val recentTransactions: List<Transaction> = emptyList(),
    val categoryBreakdowns: List<CategoryBreakdown> = emptyList(),
    val categoryBudgets: List<CategoryBudgetProgress> = emptyList(),
    val isLoading: Boolean = true,
    val hasTransactions: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMonthlySummaryUseCase: GetMonthlySummaryUseCase,
    private val calculateBudgetProgressUseCase: CalculateBudgetProgressUseCase,
    private val transactionRepository: TransactionRepository,
    private val deleteTransactionUseCase: DeleteTransactionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        val yearMonth = YearMonth.now()

        val summaryFlow: Flow<GetMonthlySummaryUseCase.MonthlySummary> =
            getMonthlySummaryUseCase(yearMonth)

        val budgetFlow: Flow<List<com.financetracker.domain.usecase.BudgetProgress>> =
            calculateBudgetProgressUseCase(yearMonth.toString())

        combine(
            summaryFlow,
            budgetFlow,
            transactionRepository.getRecentTransactions(5)
        ) { summary, budgets, recent ->
            val totalBudget = budgets.find { it.categoryId == null }?.budgetLimit
            val categoryBudgets = budgets.filter { it.categoryId != null }.map {
                CategoryBudgetProgress(
                    categoryId = it.categoryId!!,
                    categoryName = it.categoryName,
                    iconName = it.iconName,
                    colorHex = it.colorHex,
                    spent = it.spent,
                    limit = it.budgetLimit
                )
            }

            HomeUiState(
                totalSpent = summary.totalSpent,
                totalBudget = totalBudget,
                recentTransactions = recent,
                categoryBreakdowns = summary.categoryBreakdowns,
                categoryBudgets = categoryBudgets,
                isLoading = false,
                hasTransactions = summary.transactionCount > 0
            )
        }.onEach { _uiState.value = it }
            .launchIn(viewModelScope)
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            deleteTransactionUseCase(transaction)
        }
    }
}
