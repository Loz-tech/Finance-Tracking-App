package com.financetracker.data.repository

import com.financetracker.data.local.db.TransactionDao
import com.financetracker.data.local.entity.TransactionEntity
import com.financetracker.data.local.entity.TransactionSearchResult
import com.financetracker.domain.model.Category
import com.financetracker.domain.model.Transaction
import com.financetracker.domain.repository.CategoryRepository
import com.financetracker.domain.repository.TransactionRepository
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryRepository: CategoryRepository
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> = transactionDao.getAll().map { it.mapToDomain() }

    override fun getTransactionsByDateRange(start: LocalDate, end: LocalDate): Flow<List<Transaction>> =
        transactionDao.getByDateRange(start, end).map { it.mapToDomain() }

    override fun getTransactionsByCategory(categoryId: UUID): Flow<List<Transaction>> =
        transactionDao.getByCategory(categoryId).map { it.mapToDomain() }

    override fun searchTransactions(
        query: String,
        categoryIds: List<UUID>,
        start: LocalDate?,
        end: LocalDate?
    ): Flow<List<Transaction>> {
        val hasDateRange = start != null && end != null
        return when {
            hasDateRange && categoryIds.isNotEmpty() ->
                transactionDao.searchByTextCategoriesAndDateRange(query, categoryIds, start, end).map {
                    it.mapToDomain()
                }

            hasDateRange && categoryIds.isEmpty() ->
                transactionDao.searchByTextAndDateRange(query, start!!, end!!).map { it.mapToDomain() }

            !hasDateRange && categoryIds.isNotEmpty() ->
                transactionDao.searchByTextAndCategories(query, categoryIds).map { it.mapToDomain() }

            else ->
                transactionDao.searchByTextOnly(query).map { it.mapToDomain() }
        }
    }

    override fun getRecentTransactions(limit: Int): Flow<List<Transaction>> =
        transactionDao.getRecent(limit).map { it.mapToDomain() }

    override fun getTransactionsByYearMonth(yearMonth: String): Flow<List<Transaction>> =
        transactionDao.getByYearMonth(yearMonth).map { it.mapToDomain() }

    override suspend fun getTransactionById(id: UUID): Transaction? {
        val categories = categoryRepository.getAllCategories().first().associateBy { it.id }
        return transactionDao.getById(id)?.toDomain(categories)
    }

    override suspend fun getDailyTotals(start: LocalDate, end: LocalDate): Map<LocalDate, Double> =
        transactionDao.getDailyTotals(start, end).associate {
            LocalDate.parse(it.date) to it.total
        }

    override suspend fun saveTransaction(transaction: Transaction) {
        transactionDao.insert(transaction.toEntity())
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.delete(transaction.toEntity())
    }

    override suspend fun deleteAllTransactions() {
        transactionDao.deleteAll()
    }

    override suspend fun updateTransactionAmount(id: UUID, amount: BigDecimal) {
        transactionDao.updateAmount(id, amount)
    }

    override suspend fun updateTransactionAmounts(updates: List<Pair<UUID, BigDecimal>>) {
        transactionDao.updateAmounts(updates)
    }

    private fun TransactionSearchResult.toDomain(): Transaction = Transaction(
        id = id,
        amount = amount,
        originalAmount = originalAmount,
        originalCurrencyCode = originalCurrencyCode,
        note = note,
        date = date,
        category = Category(
            id = categoryId,
            name = categoryName,
            iconName = categoryIconName,
            colorHex = categoryColorHex
        ),
        createdAt = createdAt.toEpochMilli()
    )

    private fun List<TransactionSearchResult>.mapToDomain(): List<Transaction> = map { it.toDomain() }

    private suspend fun List<TransactionEntity>.mapToDomain(): List<Transaction> {
        if (isEmpty()) return emptyList()
        val categories = categoryRepository.getAllCategories().first().associateBy { it.id }
        return map { it.toDomain(categories) }
    }

    private fun TransactionEntity.toDomain(categories: Map<UUID, Category>): Transaction {
        val category = categories[categoryId] ?: Category(name = "Unknown", iconName = "MoreHoriz")
        return Transaction(
            id = id,
            amount = amount,
            originalAmount = originalAmount,
            originalCurrencyCode = originalCurrencyCode,
            note = note,
            date = date,
            category = category,
            createdAt = createdAt.toEpochMilli()
        )
    }

    private fun Transaction.toEntity() = TransactionEntity(
        id = id,
        amount = amount,
        originalAmount = originalAmount,
        originalCurrencyCode = originalCurrencyCode,
        note = note,
        date = date,
        categoryId = category.id,
        createdAt = Instant.ofEpochMilli(createdAt)
    )
}
