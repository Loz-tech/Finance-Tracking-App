package com.financetracker.data.repository

import com.financetracker.data.local.db.BudgetDao
import com.financetracker.data.local.entity.BudgetEntity
import com.financetracker.domain.model.Budget
import com.financetracker.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao
) : BudgetRepository {

    override fun getBudgetsByYearMonth(yearMonth: String): Flow<List<Budget>> =
        budgetDao.getByYearMonth(yearMonth).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getTotalBudget(yearMonth: String): Budget? =
        budgetDao.getTotalBudget(yearMonth)?.toDomain()

    override suspend fun getCategoryBudget(yearMonth: String, categoryId: UUID): Budget? =
        budgetDao.getBudgetByCategory(yearMonth, categoryId)?.toDomain()

    override suspend fun saveBudget(budget: Budget) {
        budgetDao.insert(budget.toEntity())
    }

    override suspend fun deleteAllBudgets() {
        budgetDao.deleteAll()
    }

    private fun BudgetEntity.toDomain() = Budget(
        id = id,
        categoryId = categoryId,
        yearMonth = yearMonth,
        limitAmount = limitAmount
    )

    private fun Budget.toEntity() = BudgetEntity(
        id = id,
        categoryId = categoryId,
        yearMonth = yearMonth,
        limitAmount = limitAmount
    )
}
