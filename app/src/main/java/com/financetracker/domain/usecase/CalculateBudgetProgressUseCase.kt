package com.financetracker.domain.usecase

import com.financetracker.domain.repository.BudgetRepository
import com.financetracker.domain.repository.CategoryRepository
import com.financetracker.domain.repository.TransactionRepository
import com.financetracker.domain.util.TimeProvider
import java.math.BigDecimal
import java.time.YearMonth
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class BudgetProgress(
    val categoryId: UUID?,
    val categoryName: String,
    val emoji: String,
    val colorHex: String?,
    val budgetLimit: BigDecimal,
    val spent: BigDecimal,
    val remaining: BigDecimal,
    val isOverBudget: Boolean
)

class CalculateBudgetProgressUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val timeProvider: TimeProvider
) {
    operator fun invoke(yearMonth: String? = null): Flow<List<BudgetProgress>> {
        val effectiveYearMonth = yearMonth ?: YearMonth.from(timeProvider.today()).toString()
        val ym = YearMonth.parse(effectiveYearMonth)
        val start = ym.atDay(1)
        val end = ym.atEndOfMonth()

        return combine(
            budgetRepository.getBudgetsByYearMonth(effectiveYearMonth),
            transactionRepository.getTransactionsByDateRange(start, end),
            categoryRepository.getAllCategories()
        ) { budgets, transactions, categories ->
            val categoryMap = categories.associateBy { it.id }
            val categorySpending = transactions.groupBy { it.category.id }
                .mapValues { (_, txns) -> txns.fold(BigDecimal.ZERO) { acc, t -> acc + t.amount } }

            budgets.map { budget ->
                val spent = if (budget.categoryId != null) {
                    categorySpending[budget.categoryId] ?: BigDecimal.ZERO
                } else {
                    categorySpending.values.fold(BigDecimal.ZERO) { acc, s -> acc + s }
                }

                val cat = budget.categoryId?.let { categoryMap[it] }

                BudgetProgress(
                    categoryId = budget.categoryId,
                    categoryName = cat?.name ?: "Total",
                    emoji = cat?.emoji ?: "💰",
                    colorHex = cat?.colorHex,
                    budgetLimit = budget.limitAmount,
                    spent = spent,
                    remaining = budget.limitAmount - spent,
                    isOverBudget = spent > budget.limitAmount
                )
            }
        }
    }
}
