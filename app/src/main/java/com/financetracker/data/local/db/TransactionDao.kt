package com.financetracker.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.financetracker.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity)

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: UUID): TransactionEntity?

    @Query("SELECT * FROM transactions ORDER BY date DESC, createdAt DESC")
    fun getAll(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :start AND :end ORDER BY date DESC, createdAt DESC")
    fun getByDateRange(start: LocalDate, end: LocalDate): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId ORDER BY date DESC")
    fun getByCategory(categoryId: UUID): Flow<List<TransactionEntity>>

    @Query(
        """
        SELECT * FROM transactions 
        WHERE note LIKE '%' || :query || '%' 
           OR categoryId IN (SELECT id FROM categories WHERE name LIKE '%' || :query || '%')
        ORDER BY date DESC, createdAt DESC
        """
    )
    fun searchByNote(query: String): Flow<List<TransactionEntity>>

    @Query(
        """
        SELECT date, SUM(CAST(amount AS REAL)) as total 
        FROM transactions 
        WHERE date BETWEEN :start AND :end 
        GROUP BY date 
        ORDER BY date
        """
    )
    suspend fun getDailyTotals(start: LocalDate, end: LocalDate): List<DailyTotal>

    @Query("SELECT * FROM transactions ORDER BY date DESC, createdAt DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<TransactionEntity>>

    @Query(
        """
        SELECT * FROM transactions 
        WHERE strftime('%Y-%m', date) = :yearMonth 
        ORDER BY date DESC, createdAt DESC
        """
    )
    fun getByYearMonth(yearMonth: String): Flow<List<TransactionEntity>>

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()
}

data class DailyTotal(
    val date: String,
    val total: Double
)
