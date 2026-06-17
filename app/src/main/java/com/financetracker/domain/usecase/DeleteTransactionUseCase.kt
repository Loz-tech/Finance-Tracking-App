package com.financetracker.domain.usecase

import com.financetracker.domain.model.Transaction
import com.financetracker.domain.repository.TransactionRepository
import javax.inject.Inject

/**
 * Deletes a transaction. Single source of truth for transaction deletion across ViewModels
 * and UI state holders.
 */
class DeleteTransactionUseCase @Inject constructor(private val transactionRepository: TransactionRepository) {
    suspend operator fun invoke(transaction: Transaction) {
        transactionRepository.deleteTransaction(transaction)
    }
}
