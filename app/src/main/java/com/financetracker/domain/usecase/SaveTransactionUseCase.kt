package com.financetracker.domain.usecase

import com.financetracker.domain.model.Category
import com.financetracker.domain.model.Transaction
import com.financetracker.domain.repository.SettingsRepository
import com.financetracker.domain.repository.TransactionRepository
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class SaveTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(
        category: Category?,
        amount: BigDecimal,
        note: String,
        date: LocalDate,
        editTransactionId: UUID? = null
    ): Result<Unit> {
        if (category == null) {
            return Result.failure(IllegalArgumentException("Category is required"))
        }
        if (amount <= BigDecimal.ZERO) {
            return Result.failure(IllegalArgumentException("Amount must be greater than zero"))
        }

        val prefs = settingsRepository.userPreferences.first()
        val originalAmount: BigDecimal
        val originalCurrencyCode: String
        if (editTransactionId != null) {
            val existing = transactionRepository.getTransactionById(editTransactionId)
            originalAmount = existing?.originalAmount ?: amount
            originalCurrencyCode = existing?.originalCurrencyCode ?: prefs.currencyCode
        } else {
            originalAmount = amount
            originalCurrencyCode = prefs.currencyCode
        }

        val transaction = Transaction(
            id = editTransactionId ?: UUID.randomUUID(),
            amount = amount,
            originalAmount = originalAmount,
            originalCurrencyCode = originalCurrencyCode,
            note = note,
            date = date,
            category = category
        )
        transactionRepository.saveTransaction(transaction)
        return Result.success(Unit)
    }
}
