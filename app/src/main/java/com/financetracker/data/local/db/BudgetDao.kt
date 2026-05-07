package com.financetracker.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.financetracker.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: BudgetEntity)

    @Query("SELECT * FROM budgets WHERE yearMonth = :yearMonth")
    fun getByYearMonth(yearMonth: String): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE yearMonth = :yearMonth")
    suspend fun getByYearMonthSync(yearMonth: String): List<BudgetEntity>

    @Query("SELECT * FROM budgets WHERE yearMonth = :yearMonth AND categoryId IS NULL")
    suspend fun getTotalBudget(yearMonth: String): BudgetEntity?

    @Query("SELECT * FROM budgets WHERE yearMonth = :yearMonth AND categoryId IS NOT NULL")
    fun getCategoryBudgets(yearMonth: String): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE yearMonth = :yearMonth AND categoryId = :categoryId")
    suspend fun getBudgetByCategory(yearMonth: String, categoryId: UUID): BudgetEntity?

    @Query("DELETE FROM budgets")
    suspend fun deleteAll()
}
