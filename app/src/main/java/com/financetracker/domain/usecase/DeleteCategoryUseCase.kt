package com.financetracker.domain.usecase

import com.financetracker.domain.model.Category
import com.financetracker.domain.repository.CategoryRepository
import com.financetracker.domain.repository.TransactionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class DeleteCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(category: Category): Result<Unit> {
        val transactions = transactionRepository.getTransactionsByCategory(category.id).first()
        val otherCat = categoryRepository.getAllCategories().first().find { it.name == "Other" }
            ?: Category(name = "Other", iconName = "MoreHoriz").also { categoryRepository.saveCategory(it) }
        if (otherCat.id != category.id) {
            transactions.forEach { txn ->
                transactionRepository.saveTransaction(txn.copy(category = otherCat))
            }
        }
        categoryRepository.deleteCategory(category)
        return Result.success(Unit)
    }
}
