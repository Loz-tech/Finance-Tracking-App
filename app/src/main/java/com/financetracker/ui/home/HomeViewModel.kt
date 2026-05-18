package com.financetracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.financetracker.domain.model.Transaction
import com.financetracker.domain.repository.BudgetRepository
import com.financetracker.domain.repository.CategoryRepository
import com.financetracker.domain.repository.TransactionRepository
import com.financetracker.ui.components.DonutSegment
import com.financetracker.ui.theme.ChartColors
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class CategoryBudgetProgress(
    val categoryId: UUID,
    val categoryName: String,
    val emoji: String,
    val colorHex: String?,
    val spent: BigDecimal,
    val limit: BigDecimal
)

data class HomeUiState(
    val totalSpent: BigDecimal = BigDecimal.ZERO,
    val totalBudget: BigDecimal? = null,
    val recentTransactions: List<Transaction> = emptyList(),
    val categorySegments: List<DonutSegment> = emptyList(),
    val categoryBudgets: List<CategoryBudgetProgress> = emptyList(),
    val isLoading: Boolean = true,
    val hasTransactions: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        val now = LocalDate.now()
        val yearMonth = YearMonth.now().toString()
        val monthStart = now.withDayOfMonth(1)
        val monthEnd = now.withDayOfMonth(now.lengthOfMonth())

        combine(
            transactionRepository.getTransactionsByDateRange(monthStart, monthEnd),
            transactionRepository.getRecentTransactions(5),
            budgetRepository.getBudgetsByYearMonth(yearMonth),
            categoryRepository.getAllCategories()
        ) { monthlyTransactions, recentTransactions, budgets, categories ->
            val totalSpent = monthlyTransactions.fold(BigDecimal.ZERO) { acc, t -> acc + t.amount }
            val totalBudget = budgets.find { it.categoryId == null }
            val hasTransactions = monthlyTransactions.isNotEmpty()

            // Group by category for donut
            val categoryGroups = monthlyTransactions.groupBy { it.category }
            val chartColors = ChartColors
            val segments = categoryGroups.entries.mapIndexed { i, (cat, txns) ->
                DonutSegment(
                    label = cat.name,
                    emoji = cat.emoji,
                    value = txns.sumOf { it.amount.toDouble() }.toFloat(),
                    color = chartColors[i % chartColors.size]
                )
            }

            // Per-category budget progress
            val spentByCategory = monthlyTransactions.groupBy {
                it.category.id
            }.mapValues { it.value.sumOf { t -> t.amount } }
            val categoryBudgets = categories.mapNotNull { cat ->
                val limit = budgets.find { it.categoryId == cat.id }?.limitAmount ?: return@mapNotNull null
                if (limit <= BigDecimal.ZERO) return@mapNotNull null
                CategoryBudgetProgress(
                    categoryId = cat.id,
                    categoryName = cat.name,
                    emoji = cat.emoji,
                    colorHex = cat.colorHex,
                    spent = spentByCategory[cat.id] ?: BigDecimal.ZERO,
                    limit = limit
                )
            }.sortedByDescending { it.spent }

            HomeUiState(
                totalSpent = totalSpent,
                totalBudget = totalBudget?.limitAmount,
                recentTransactions = recentTransactions,
                categorySegments = segments,
                categoryBudgets = categoryBudgets,
                isLoading = false,
                hasTransactions = hasTransactions
            )
        }.onEach { _uiState.value = it }
            .launchIn(viewModelScope)
    }
}
