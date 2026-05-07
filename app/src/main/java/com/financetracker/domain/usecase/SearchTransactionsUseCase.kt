package com.financetracker.domain.usecase

import com.financetracker.domain.model.Transaction
import com.financetracker.domain.repository.CategoryRepository
import com.financetracker.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject

class SearchTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(query: String, categoryIds: Set<UUID> = emptySet()): List<Transaction> {
        if (query.isBlank() && categoryIds.isEmpty()) return emptyList()

        val all = if (query.isNotBlank()) {
            transactionRepository.searchTransactions(query.trim()).first()
        } else {
            transactionRepository.getAllTransactions().first()
        }

        return if (categoryIds.isNotEmpty()) {
            all.filter { it.category.id in categoryIds }
        } else all
    }
}
