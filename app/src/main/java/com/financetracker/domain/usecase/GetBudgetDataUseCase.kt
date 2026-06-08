package com.financetracker.domain.usecase

import com.financetracker.domain.model.Category
import com.financetracker.domain.repository.BudgetRepository
import com.financetracker.domain.repository.CategoryRepository
import java.math.BigDecimal
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.first

data class BudgetData(val totalBudget: BigDecimal, val categoryBudgets: List<CategoryBudget>)

data class CategoryBudget(val category: Category, val limit: BigDecimal, val spent: BigDecimal = BigDecimal.ZERO)

class GetBudgetDataUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(yearMonth: YearMonth): BudgetData {
        val ym = yearMonth.toString()
        budgetRepository.deleteDuplicateBudgets()
        val totalBudget = budgetRepository.getTotalBudget(ym)?.limitAmount ?: BigDecimal.ZERO
        val categories = categoryRepository.getAllCategories().first()
        val existingBudgets = budgetRepository.getBudgetsByYearMonth(ym).first()
        val prevBudgets = budgetRepository.getBudgetsByYearMonth(yearMonth.minusMonths(1).toString()).first()

        val categoryBudgets = categories.map { cat ->
            val existing = existingBudgets.find { it.categoryId == cat.id }
            val prev = prevBudgets.find { it.categoryId == cat.id }
            val limit = existing?.limitAmount ?: prev?.limitAmount ?: BigDecimal.ZERO
            CategoryBudget(cat, limit, BigDecimal.ZERO)
        }

        return BudgetData(totalBudget, categoryBudgets)
    }
}
