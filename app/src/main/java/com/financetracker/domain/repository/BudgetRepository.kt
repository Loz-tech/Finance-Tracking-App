package com.financetracker.domain.repository

import com.financetracker.domain.model.Budget
import java.util.UUID
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getBudgetsByYearMonth(yearMonth: String): Flow<List<Budget>>
    suspend fun getTotalBudget(yearMonth: String): Budget?
    suspend fun getCategoryBudget(yearMonth: String, categoryId: UUID): Budget?
    suspend fun saveBudget(budget: Budget)
    suspend fun deleteAllBudgets()
    suspend fun deleteDuplicateBudgets()
}
