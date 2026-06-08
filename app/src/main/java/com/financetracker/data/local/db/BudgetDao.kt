package com.financetracker.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.financetracker.data.local.entity.BudgetEntity
import java.math.BigDecimal
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

    @Query("SELECT * FROM budgets")
    fun getAll(): Flow<List<BudgetEntity>>

    @Query("DELETE FROM budgets")
    suspend fun deleteAll()

    @Query("UPDATE budgets SET limitAmount = :limitAmount WHERE id = :id")
    suspend fun updateLimitAmount(id: UUID, limitAmount: BigDecimal)

    @Transaction
    suspend fun updateLimitAmounts(updates: List<Pair<UUID, BigDecimal>>) {
        updates.forEach { (id, limitAmount) ->
            updateLimitAmount(id, limitAmount)
        }
    }
}
