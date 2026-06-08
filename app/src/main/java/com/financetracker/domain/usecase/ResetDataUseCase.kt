package com.financetracker.domain.usecase

import com.financetracker.domain.repository.CategoryRepository
import com.financetracker.domain.repository.TransactionRepository
import javax.inject.Inject

class ResetDataUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) {

    suspend operator fun invoke(): Result<Unit> {
        transactionRepository.deleteAllTransactions()
        categoryRepository.seedDefaultCategories()
        return Result.success(Unit)
    }
}
