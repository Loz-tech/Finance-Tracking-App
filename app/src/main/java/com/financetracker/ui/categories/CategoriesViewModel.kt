package com.financetracker.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.financetracker.domain.model.Category
import com.financetracker.domain.repository.CategoryRepository
import com.financetracker.domain.usecase.AddCategoryUseCase
import com.financetracker.domain.usecase.DeleteCategoryUseCase
import com.financetracker.domain.usecase.GetCategoriesWithSpendingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
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
    private val getCategoriesWithSpendingUseCase: GetCategoriesWithSpendingUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState

    init {
        viewModelScope.launch {
            getCategoriesWithSpendingUseCase(YearMonth.now()).collect { categorySpendings ->
                val withProgress = categorySpendings.map { cs ->
                    CategoryWithProgress(cs.category, cs.spent, null, false)
                }
                _uiState.value = CategoriesUiState(categoriesWithProgress = withProgress, isLoading = false)
            }
        }
    }

    fun addCategory(name: String, iconName: String) {
        viewModelScope.launch {
            addCategoryUseCase(name, iconName)
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch { categoryRepository.saveCategory(category) }
    }

    fun updateCategoryIcon(category: Category, iconName: String) {
        viewModelScope.launch {
            categoryRepository.saveCategory(category.copy(iconName = iconName))
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            deleteCategoryUseCase(category)
        }
    }
}
