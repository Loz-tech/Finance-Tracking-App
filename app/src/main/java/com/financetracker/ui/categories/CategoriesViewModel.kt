package com.financetracker.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.financetracker.domain.model.Category
import com.financetracker.domain.repository.CategoryRepository
import com.financetracker.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class CategoryWithProgress(
    val category: Category,
    val spent: BigDecimal,
    val budgetLimit: BigDecimal?,
    val isOverBudget: Boolean
)

data class CategoriesUiState(
    val categoriesWithProgress: List<CategoryWithProgress> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState

    init {
        viewModelScope.launch {
            combine(
                categoryRepository.getAllCategories(),
                transactionRepository.getTransactionsByYearMonth(YearMonth.now().toString())
            ) { categories, transactions ->
                val categorySpending = transactions.groupBy { it.category.id }
                    .mapValues { (_, txns) -> txns.fold(BigDecimal.ZERO) { acc, t -> acc + t.amount } }

                val withProgress = categories.map { cat ->
                    val spent = categorySpending[cat.id] ?: BigDecimal.ZERO
                    CategoryWithProgress(cat, spent, null, false)
                }

                CategoriesUiState(categoriesWithProgress = withProgress, isLoading = false)
            }.collect { _uiState.value = it }
        }
    }

    fun addCategory(name: String, emoji: String) {
        viewModelScope.launch {
            val maxOrder = categoryRepository.getAllCategories().first().maxOfOrNull { it.sortOrder } ?: -1
            categoryRepository.saveCategory(Category(name = name, emoji = emoji, sortOrder = maxOrder + 1))
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch { categoryRepository.saveCategory(category) }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            val transactions = transactionRepository.getTransactionsByCategory(category.id).first()
            val otherCat = categoryRepository.getAllCategories().first().find { it.name == "Other" }
                ?: Category(name = "Other", emoji = "📦")
            if (otherCat.id != category.id) {
                transactions.forEach { txn ->
                    transactionRepository.saveTransaction(txn.copy(category = otherCat))
                }
            }
            categoryRepository.deleteCategory(category)
        }
    }
}
