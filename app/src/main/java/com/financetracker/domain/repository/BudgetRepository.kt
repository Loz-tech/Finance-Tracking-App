package com.financetracker.domain.repository

import com.financetracker.domain.model.Budget
import java.math.BigDecimal
import java.util.UUID
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getBudgetsByYearMonth(yearMonth: String): Flow<List<Budget>>
    fun getAllBudgets(): Flow<List<Budget>>
    suspend fun getTotalBudget(yearMonth: String): Budget?
    suspend fun getCategoryBudget(yearMonth: String, categoryId: UUID): Budget?
    suspend fun saveBudget(budget: Budget)
    suspend fun deleteAllBudgets()
    suspend fun deleteDuplicateBudgets()
    suspend fun updateBudgetLimitAmount(id: UUID, limitAmount: BigDecimal)
    suspend fun updateBudgetLimitAmounts(updates: List<Pair<UUID, BigDecimal>>)
}
