package com.financetracker.domain.usecase

import com.financetracker.domain.model.Budget
import com.financetracker.domain.model.Category
import com.financetracker.domain.model.Transaction
import com.financetracker.domain.repository.BudgetRepository
import com.financetracker.domain.repository.ExchangeRateRepository
import com.financetracker.domain.repository.TransactionRepository
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecalculateTransactionsUseCaseTest {

    private val cat = Category(name = "Food", iconName = "🍔", colorHex = "#FF0000")

    private val txn = Transaction(
        id = UUID.randomUUID(),
        amount = BigDecimal("100.00"),
        originalAmount = BigDecimal("100.00"),
        originalCurrencyCode = "USD",
        date = LocalDate.of(2024, 6, 1),
        category = cat
    )

    private val budget = Budget(
        id = UUID.randomUUID(),
        yearMonth = "2024-06",
        limitAmount = BigDecimal("500.00"),
        originalLimitAmount = BigDecimal("500.00"),
        originalCurrencyCode = "USD"
    )

    private open inner class FakeTransactionRepo : TransactionRepository {
        val updatedAmounts = mutableMapOf<UUID, BigDecimal>()
        override fun getAllTransactions(): Flow<List<Transaction>> = flowOf(listOf(txn))
        override fun getTransactionsByDateRange(start: LocalDate, end: LocalDate) = flowOf(emptyList<Transaction>())
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
        override suspend fun updateTransactionAmount(id: UUID, amount: BigDecimal) {
            updatedAmounts[id] = amount
        }
        override suspend fun updateTransactionAmounts(updates: List<Pair<UUID, BigDecimal>>) {
            updates.forEach { (id, amount) -> updatedAmounts[id] = amount }
        }
    }

    private open inner class FakeBudgetRepo : BudgetRepository {
        val updatedLimits = mutableMapOf<UUID, BigDecimal>()
        override fun getBudgetsByYearMonth(yearMonth: String): Flow<List<Budget>> = flowOf(emptyList())
        override fun getAllBudgets(): Flow<List<Budget>> = flowOf(listOf(budget))
        override suspend fun getTotalBudget(yearMonth: String): Budget? = null
        override suspend fun getCategoryBudget(yearMonth: String, categoryId: UUID): Budget? = null
        override suspend fun saveBudget(budget: Budget) {}
        override suspend fun deleteAllBudgets() {}
        override suspend fun deleteDuplicateBudgets() {}
        override suspend fun updateBudgetLimitAmount(id: UUID, limitAmount: BigDecimal) {
            updatedLimits[id] = limitAmount
        }
        override suspend fun updateBudgetLimitAmounts(updates: List<Pair<UUID, BigDecimal>>) {
            updates.forEach { (id, limit) -> updatedLimits[id] = limit }
        }
    }

    private open inner class FakeExchangeRateRepo : ExchangeRateRepository {
        override fun getRate(
            baseCode: String,
            targetCode: String
        ): Flow<com.financetracker.domain.model.ExchangeRate?> = flowOf(null)
        override suspend fun refreshRates(baseCode: String): Result<Map<String, BigDecimal>> = Result.success(
            mapOf(
                "USD" to BigDecimal.ONE,
                "EUR" to BigDecimal("0.92")
            )
        )
        override suspend fun setManualRate(baseCode: String, targetCode: String, rate: BigDecimal) {}
        override suspend fun clearManualRate(baseCode: String, targetCode: String) {}
        override suspend fun getManualRate(baseCode: String, targetCode: String): BigDecimal? = null
    }

    @Test
    fun `recalculates transactions and budgets to new currency`() = runTest {
        val txnRepo = FakeTransactionRepo()
        val budgetRepo = FakeBudgetRepo()
        val exchangeRepo = FakeExchangeRateRepo()
        val convertUseCase = ConvertAmountUseCase()

        val useCase = RecalculateTransactionsUseCase(txnRepo, budgetRepo, exchangeRepo, convertUseCase)
        val result = useCase("USD", "EUR")

        assertTrue(result.isSuccess)
        assertEquals(BigDecimal("92.00"), txnRepo.updatedAmounts[txn.id])
        assertEquals(BigDecimal("460.00"), budgetRepo.updatedLimits[budget.id])
    }

    @Test
    fun `returns failure when rate fetch fails`() = runTest {
        val txnRepo = FakeTransactionRepo()
        val budgetRepo = FakeBudgetRepo()
        val exchangeRepo = object : FakeExchangeRateRepo() {
            override suspend fun refreshRates(baseCode: String): Result<Map<String, BigDecimal>> =
                Result.failure(Exception("Network error"))
        }
        val convertUseCase = ConvertAmountUseCase()

        val useCase = RecalculateTransactionsUseCase(txnRepo, budgetRepo, exchangeRepo, convertUseCase)
        val result = useCase("USD", "EUR")

        assertTrue(result.isFailure)
    }

    @Test
    fun `succeeds with empty transactions and budgets`() = runTest {
        val txnRepo = object : FakeTransactionRepo() {
            override fun getAllTransactions(): Flow<List<Transaction>> = flowOf(emptyList())
        }
        val budgetRepo = object : FakeBudgetRepo() {
            override fun getAllBudgets(): Flow<List<Budget>> = flowOf(emptyList())
        }
        val exchangeRepo = FakeExchangeRateRepo()
        val convertUseCase = ConvertAmountUseCase()

        val useCase = RecalculateTransactionsUseCase(txnRepo, budgetRepo, exchangeRepo, convertUseCase)
        val result = useCase("USD", "EUR")

        assertTrue(result.isSuccess)
        assertTrue(txnRepo.updatedAmounts.isEmpty())
        assertTrue(budgetRepo.updatedLimits.isEmpty())
    }

    @Test
    fun `uses manual rate when available`() = runTest {
        val txnRepo = FakeTransactionRepo()
        val budgetRepo = FakeBudgetRepo()
        val exchangeRepo = object : FakeExchangeRateRepo() {
            override suspend fun getManualRate(baseCode: String, targetCode: String): BigDecimal? =
                if (baseCode == "USD" && targetCode == "EUR") BigDecimal("0.85") else null
        }
        val convertUseCase = ConvertAmountUseCase()

        val useCase = RecalculateTransactionsUseCase(txnRepo, budgetRepo, exchangeRepo, convertUseCase)
        val result = useCase("USD", "EUR")

        assertTrue(result.isSuccess)
        assertEquals(BigDecimal("85.00"), txnRepo.updatedAmounts[txn.id])
        assertEquals(BigDecimal("425.00"), budgetRepo.updatedLimits[budget.id])
    }

    @Test
    fun `returns failure when conversion rate is missing`() = runTest {
        val txnRepo = FakeTransactionRepo()
        val budgetRepo = FakeBudgetRepo()
        val exchangeRepo = object : FakeExchangeRateRepo() {
            override suspend fun refreshRates(baseCode: String): Result<Map<String, BigDecimal>> =
                Result.success(mapOf("USD" to BigDecimal.ONE))
        }
        val convertUseCase = ConvertAmountUseCase()

        val useCase = RecalculateTransactionsUseCase(txnRepo, budgetRepo, exchangeRepo, convertUseCase)
        val result = useCase("USD", "EUR")

        assertTrue(result.isFailure)
    }
}
