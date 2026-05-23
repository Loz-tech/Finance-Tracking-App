package com.financetracker.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.financetracker.data.local.entity.TransactionEntity
import com.financetracker.data.local.entity.TransactionSearchResult
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.flow.Flow

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
        SELECT t.id, t.amount, t.note, t.date, t.categoryId, t.createdAt,
               c.name AS categoryName, c.iconName AS categoryIconName, c.colorHex AS categoryColorHex
        FROM transactions t
        JOIN categories c ON t.categoryId = c.id
        WHERE t.note LIKE '%' || :query || '%'
           OR c.name LIKE '%' || :query || '%'
        ORDER BY t.date DESC, t.createdAt DESC
        """
    )
    fun searchByTextOnly(query: String): Flow<List<TransactionSearchResult>>

    @Query(
        """
        SELECT t.id, t.amount, t.note, t.date, t.categoryId, t.createdAt,
               c.name AS categoryName, c.iconName AS categoryIconName, c.colorHex AS categoryColorHex
        FROM transactions t
        JOIN categories c ON t.categoryId = c.id
        WHERE (t.note LIKE '%' || :query || '%' OR c.name LIKE '%' || :query || '%')
          AND t.categoryId IN (:categoryIds)
        ORDER BY t.date DESC, t.createdAt DESC
        """
    )
    fun searchByTextAndCategories(query: String, categoryIds: List<UUID>): Flow<List<TransactionSearchResult>>

    @Query(
        """
        SELECT t.id, t.amount, t.note, t.date, t.categoryId, t.createdAt,
               c.name AS categoryName, c.iconName AS categoryIconName, c.colorHex AS categoryColorHex
        FROM transactions t
        JOIN categories c ON t.categoryId = c.id
        WHERE (t.note LIKE '%' || :query || '%' OR c.name LIKE '%' || :query || '%')
          AND t.date BETWEEN :start AND :end
        ORDER BY t.date DESC, t.createdAt DESC
        """
    )
    fun searchByTextAndDateRange(query: String, start: LocalDate, end: LocalDate): Flow<List<TransactionSearchResult>>

    @Query(
        """
        SELECT t.id, t.amount, t.note, t.date, t.categoryId, t.createdAt,
               c.name AS categoryName, c.iconName AS categoryIconName, c.colorHex AS categoryColorHex
        FROM transactions t
        JOIN categories c ON t.categoryId = c.id
        WHERE (t.note LIKE '%' || :query || '%' OR c.name LIKE '%' || :query || '%')
          AND t.categoryId IN (:categoryIds)
          AND t.date BETWEEN :start AND :end
        ORDER BY t.date DESC, t.createdAt DESC
        """
    )
    fun searchByTextCategoriesAndDateRange(
        query: String,
        categoryIds: List<UUID>,
        start: LocalDate,
        end: LocalDate
    ): Flow<List<TransactionSearchResult>>

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

data class DailyTotal(val date: String, val total: Double)
