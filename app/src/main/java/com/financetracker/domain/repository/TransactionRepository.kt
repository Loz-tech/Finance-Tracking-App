package com.financetracker.domain.repository

import com.financetracker.domain.model.Transaction
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    fun getTransactionsByDateRange(start: LocalDate, end: LocalDate): Flow<List<Transaction>>
    fun getTransactionsByCategory(categoryId: UUID): Flow<List<Transaction>>
    fun searchTransactions(
        query: String,
        categoryIds: List<UUID> = emptyList(),
        start: LocalDate? = null,
        end: LocalDate? = null
    ): Flow<List<Transaction>>
    fun getRecentTransactions(limit: Int = 5): Flow<List<Transaction>>
    fun getTransactionsByYearMonth(yearMonth: String): Flow<List<Transaction>>
    suspend fun getTransactionById(id: UUID): Transaction?
    suspend fun getDailyTotals(start: LocalDate, end: LocalDate): Map<LocalDate, Double>
    suspend fun saveTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)
    suspend fun deleteAllTransactions()
    suspend fun updateTransactionAmount(id: UUID, amount: BigDecimal)
    suspend fun updateTransactionAmounts(updates: List<Pair<UUID, BigDecimal>>)
}
