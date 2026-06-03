package com.financetracker.domain.usecase

import com.financetracker.domain.repository.BudgetRepository
import com.financetracker.domain.repository.ExchangeRateRepository
import com.financetracker.domain.repository.TransactionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class RecalculateTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository,
    private val exchangeRateRepository: ExchangeRateRepository,
    private val convertAmountUseCase: ConvertAmountUseCase
) {

    suspend operator fun invoke(currentCurrencyCode: String, newCurrencyCode: String): Result<Unit> {
        val ratesResult = exchangeRateRepository.refreshRates("USD")
        if (ratesResult.isFailure) {
            return Result.failure(ratesResult.exceptionOrNull() ?: Exception("Failed to fetch rates"))
        }

        val rates = ratesResult.getOrThrow()

        val manualRate = exchangeRateRepository.getManualRate(currentCurrencyCode, newCurrencyCode)

        val transactions = transactionRepository.getAllTransactions().first()
        val transactionUpdates = mutableListOf<Pair<java.util.UUID, java.math.BigDecimal>>()
        for (transaction in transactions) {
            val conversion = convertAmountUseCase(
                transaction.originalAmount,
                transaction.originalCurrencyCode,
                newCurrencyCode,
                rates,
                if (transaction.originalCurrencyCode == currentCurrencyCode) manualRate else null
            )
            if (conversion.isFailure) {
                return Result.failure(conversion.exceptionOrNull() ?: Exception("Conversion failed"))
            }
            transactionUpdates.add(transaction.id to conversion.getOrThrow())
        }
        if (transactionUpdates.isNotEmpty()) {
            transactionRepository.updateTransactionAmounts(transactionUpdates)
        }

        val allBudgets = budgetRepository.getAllBudgets().first()
        val budgetUpdates = mutableListOf<Pair<java.util.UUID, java.math.BigDecimal>>()
        for (budget in allBudgets) {
            val conversion = convertAmountUseCase(
                budget.originalLimitAmount,
                budget.originalCurrencyCode,
                newCurrencyCode,
                rates,
                if (budget.originalCurrencyCode == currentCurrencyCode) manualRate else null
            )
            if (conversion.isFailure) {
                return Result.failure(conversion.exceptionOrNull() ?: Exception("Conversion failed"))
            }
            budgetUpdates.add(budget.id to conversion.getOrThrow())
        }
        if (budgetUpdates.isNotEmpty()) {
            budgetRepository.updateBudgetLimitAmounts(budgetUpdates)
        }

        return Result.success(Unit)
    }
}
