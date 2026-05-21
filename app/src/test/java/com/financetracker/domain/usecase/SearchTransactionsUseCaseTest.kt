package com.financetracker.domain.usecase

import app.cash.turbine.test
import com.financetracker.domain.model.Category
import com.financetracker.domain.model.DateFilter
import com.financetracker.domain.model.QuickChip
import com.financetracker.domain.model.SearchCriteria
import com.financetracker.domain.model.Transaction
import com.financetracker.domain.repository.TransactionRepository
import com.financetracker.domain.util.TimeProvider
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchTransactionsUseCaseTest {

    private val fixedToday = LocalDate.of(2024, 6, 15)

    private class FakeTimeProvider(private val todayValue: LocalDate) : TimeProvider {
        override fun today(): LocalDate = todayValue
    }

    private class FakeTransactionRepository(private val today: LocalDate) : TransactionRepository {
        override fun searchTransactions(query: String, categoryIds: List<UUID>, start: LocalDate?, end: LocalDate?) =
            flowOf(
                listOf(
                    Transaction(
                        id = UUID.randomUUID(),
                        amount = BigDecimal.TEN,
                        note = query,
                        date = start ?: today,
                        category = Category(name = "Test", emoji = "🧪")
                    )
                )
            )

        override fun getAllTransactions() = flowOf(emptyList<Transaction>())

        override fun getTransactionsByDateRange(start: LocalDate, end: LocalDate) = flowOf(emptyList<Transaction>())

        override fun getTransactionsByCategory(categoryId: UUID) = flowOf(emptyList<Transaction>())

        override fun getRecentTransactions(limit: Int) = flowOf(emptyList<Transaction>())

        override fun getTransactionsByYearMonth(yearMonth: String) = flowOf(emptyList<Transaction>())

        override suspend fun getTransactionById(id: UUID): Transaction? = null

        override suspend fun getDailyTotals(start: LocalDate, end: LocalDate) = emptyMap<LocalDate, Double>()

        override suspend fun saveTransaction(transaction: Transaction) {}

        override suspend fun deleteTransaction(transaction: Transaction) {}

        override suspend fun deleteAllTransactions() {}
    }

    @Test
    fun `when criteria is empty returns empty list`() = runTest {
        val repo = FakeTransactionRepository(fixedToday)
        val useCase = SearchTransactionsUseCase(repo, FakeTimeProvider(fixedToday))

        val criteria = MutableSharedFlow<SearchCriteria>(replay = 1)
        criteria.emit(SearchCriteria(dateFilter = DateFilter.None))

        useCase(criteria).test {
            assertEquals(emptyList<Transaction>(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when quick chip selected resolves date range and calls repo`() = runTest {
        val repo = FakeTransactionRepository(fixedToday)
        val useCase = SearchTransactionsUseCase(repo, FakeTimeProvider(fixedToday))

        val criteria = MutableSharedFlow<SearchCriteria>(replay = 1)
        criteria.emit(SearchCriteria(query = "coffee", dateFilter = DateFilter.Quick(QuickChip.TODAY)))

        useCase(criteria).test {
            val result = awaitItem()
            assertEquals(fixedToday, result.first().date)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when custom date range selected passes exact dates`() = runTest {
        val repo = FakeTransactionRepository(fixedToday)
        val useCase = SearchTransactionsUseCase(repo, FakeTimeProvider(fixedToday))

        val start = LocalDate.of(2024, 1, 1)
        val end = LocalDate.of(2024, 1, 31)

        val criteria = MutableSharedFlow<SearchCriteria>(replay = 1)
        criteria.emit(SearchCriteria(dateFilter = DateFilter.Custom(start, end)))

        useCase(criteria).test {
            val result = awaitItem()
            assertEquals(start, result.first().date)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
