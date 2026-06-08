package com.financetracker.domain.usecase

import com.financetracker.domain.model.Category
import com.financetracker.domain.repository.CategoryRepository
import com.financetracker.domain.repository.TransactionRepository
import java.math.BigDecimal
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class CategorySpending(val category: Category, val spent: BigDecimal)

class GetCategoriesWithSpendingUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(yearMonth: YearMonth): Flow<List<CategorySpending>> = combine(
        categoryRepository.getAllCategories(),
        transactionRepository.getTransactionsByYearMonth(yearMonth.toString())
    ) { categories, transactions ->
        val categorySpending = transactions.groupBy { it.category.id }
            .mapValues { (_, txns) -> txns.sumOf { it.amount } }

        categories.map { cat ->
            val spent = categorySpending[cat.id] ?: BigDecimal.ZERO
            CategorySpending(cat, spent)
        }
    }
}
