package com.financetracker.domain.usecase

import com.financetracker.domain.model.Category
import com.financetracker.domain.model.Transaction
import com.financetracker.domain.repository.TransactionRepository
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetMonthlySummaryUseCaseTest {

    private val catA = Category(name = "Food", iconName = "🍔", colorHex = "#FF0000")
    private val catB = Category(name = "Transport", iconName = "🚌", colorHex = "#00FF00")

    private val txn1 = Transaction(
        id = UUID.randomUUID(),
        amount = BigDecimal("10.00"),
        date = LocalDate.of(2024, 6, 1),
        category = catA
    )
    private val txn2 = Transaction(
        id = UUID.randomUUID(),
        amount = BigDecimal("20.00"),
        date = LocalDate.of(2024, 6, 2),
        category = catA
    )
    private val txn3 = Transaction(
        id = UUID.randomUUID(),
        amount = BigDecimal("5.00"),
        date = LocalDate.of(2024, 6, 3),
        category = catB
    )

    private class FakeRepo(private val transactions: List<Transaction>) : TransactionRepository {
        override fun getTransactionsByDateRange(start: LocalDate, end: LocalDate) = flowOf(transactions)

        override fun getAllTransactions() = flowOf(emptyList<Transaction>())

        override fun getTransactionsByCategory(categoryId: UUID) = flowOf(emptyList<Transaction>())

        override fun searchTransactions(query: String, categoryIds: List<UUID>, start: LocalDate?, end: LocalDate?) =
            flowOf(emptyList<Transaction>())

        override fun getRecentTransactions(limit: Int) = flowOf(emptyList<Transaction>())

        override fun getTransactionsByYearMonth(yearMonth: String) = flowOf(emptyList<Transaction>())

        override suspend fun getTransactionById(id: UUID): Transaction? = null

        override suspend fun getDailyTotals(start: LocalDate, end: LocalDate) = emptyMap<LocalDate, Double>()

        override suspend fun saveTransaction(transaction: Transaction) {}

        override suspend fun deleteTransaction(transaction: Transaction) {}

        override suspend fun deleteAllTransactions() {}

        override suspend fun updateTransactionAmount(id: UUID, amount: BigDecimal) {}

        override suspend fun updateTransactionAmounts(updates: List<Pair<UUID, BigDecimal>>) {}
    }

    @Test
    fun `calculates total spent and daily average correctly`() = runTest {
        val repo = FakeRepo(listOf(txn1, txn2, txn3))
        val useCase = GetMonthlySummaryUseCase(repo)

        val result = useCase(YearMonth.of(2024, 6))

        result.collect { summary ->
            assertEquals(BigDecimal("35.00"), summary.totalSpent)
            assertEquals(BigDecimal("35.00") / BigDecimal(30), summary.dailyAverage)
            assertEquals(3, summary.transactionCount)
        }
    }

    @Test
    fun `groups categories and assigns breakdowns`() = runTest {
        val repo = FakeRepo(listOf(txn1, txn2, txn3))
        val useCase = GetMonthlySummaryUseCase(repo)

        val result = useCase(YearMonth.of(2024, 6))

        result.collect { summary ->
            assertEquals(2, summary.categoryBreakdowns.size)
            val food = summary.categoryBreakdowns.find { it.name == "Food" }!!
            assertEquals(BigDecimal("30.00"), food.amount)
            assertEquals("🍔", food.iconName)
        }
    }
}
