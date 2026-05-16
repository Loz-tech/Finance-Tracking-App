package com.financetracker.domain.usecase

import com.financetracker.domain.repository.BudgetRepository
import com.financetracker.domain.repository.TransactionRepository
import java.math.BigDecimal
import java.time.YearMonth
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.first

data class BudgetProgress(
    val categoryId: UUID?,
    val budgetLimit: BigDecimal,
    val spent: BigDecimal,
    val remaining: BigDecimal,
    val isOverBudget: Boolean
)

class CalculateBudgetProgressUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(yearMonth: String = YearMonth.now().toString()): List<BudgetProgress> {
        val budgets = budgetRepository.getBudgetsByYearMonth(yearMonth).first()
        val ym = YearMonth.parse(yearMonth)
        val transactions = transactionRepository.getTransactionsByDateRange(ym.atDay(1), ym.atEndOfMonth()).first()
        val categorySpending = transactions.groupBy { it.category.id }
            .mapValues { (_, txns) -> txns.fold(BigDecimal.ZERO) { acc, t -> acc + t.amount } }

        return budgets.map { budget ->
            val spent = if (budget.categoryId != null) {
                categorySpending[budget.categoryId] ?: BigDecimal.ZERO
            } else {
                budgets.fold(BigDecimal.ZERO) { acc, b -> acc + (categorySpending[b.categoryId] ?: BigDecimal.ZERO) }
            }
            BudgetProgress(
                budget.categoryId,
                budget.limitAmount,
                spent,
                budget.limitAmount - spent,
                spent > budget.limitAmount
            )
        }
    }
}
