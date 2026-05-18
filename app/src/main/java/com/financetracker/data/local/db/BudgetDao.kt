package com.financetracker.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.financetracker.data.local.entity.BudgetEntity
import java.util.UUID
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: BudgetEntity)

    @Query("SELECT * FROM budgets WHERE yearMonth = :yearMonth ORDER BY rowid DESC")
    fun getByYearMonth(yearMonth: String): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE yearMonth = :yearMonth ORDER BY rowid DESC")
    suspend fun getByYearMonthSync(yearMonth: String): List<BudgetEntity>

    @Query("SELECT * FROM budgets WHERE yearMonth = :yearMonth AND categoryId IS NULL ORDER BY rowid DESC LIMIT 1")
    suspend fun getTotalBudget(yearMonth: String): BudgetEntity?

    @Query("SELECT * FROM budgets WHERE yearMonth = :yearMonth AND categoryId IS NOT NULL ORDER BY rowid DESC")
    fun getCategoryBudgets(yearMonth: String): Flow<List<BudgetEntity>>

    @Query(
        "SELECT * FROM budgets WHERE yearMonth = :yearMonth AND categoryId = :categoryId ORDER BY rowid DESC LIMIT 1"
    )
    suspend fun getBudgetByCategory(yearMonth: String, categoryId: UUID): BudgetEntity?

    @Query(
        """
        DELETE FROM budgets
        WHERE rowid NOT IN (
            SELECT MAX(rowid)
            FROM budgets
            GROUP BY yearMonth, categoryId
        )
    """
    )
    suspend fun deleteDuplicateBudgets()

    @Query("DELETE FROM budgets")
    suspend fun deleteAll()
}
