package com.financetracker.domain.repository

import com.financetracker.domain.model.Budget
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface BudgetRepository {
    fun getBudgetsByYearMonth(yearMonth: String): Flow<List<Budget>>
    suspend fun getTotalBudget(yearMonth: String): Budget?
    suspend fun getCategoryBudget(yearMonth: String, categoryId: UUID): Budget?
    suspend fun saveBudget(budget: Budget)
    suspend fun deleteAllBudgets()
}
