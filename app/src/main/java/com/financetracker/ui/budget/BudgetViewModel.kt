package com.financetracker.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.financetracker.domain.model.Category
import com.financetracker.domain.usecase.GetBudgetDataUseCase
import com.financetracker.domain.usecase.SaveBudgetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CategoryBudgetSliders(val category: Category, val limit: BigDecimal, val spent: BigDecimal)

data class BudgetUiState(
    val totalBudget: BigDecimal = BigDecimal.ZERO,
    val categorySliders: List<CategoryBudgetSliders> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val getBudgetDataUseCase: GetBudgetDataUseCase,
    private val saveBudgetUseCase: SaveBudgetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState: StateFlow<BudgetUiState> = _uiState

    init {
        loadBudget()
    }

    private fun loadBudget() {
        viewModelScope.launch {
            val data = getBudgetDataUseCase(YearMonth.now())
            _uiState.value = BudgetUiState(
                totalBudget = data.totalBudget,
                categorySliders = data.categoryBudgets.map {
                    CategoryBudgetSliders(it.category, it.limit, it.spent)
                },
                isLoading = false
            )
        }
    }

    fun setTotalBudget(amount: BigDecimal) {
        _uiState.value = _uiState.value.copy(totalBudget = amount)
        viewModelScope.launch {
            val yearMonth = YearMonth.now().toString()
            saveBudgetUseCase(categoryId = null, amount = amount, yearMonth = yearMonth)
        }
    }

    fun setCategoryBudget(category: Category, amount: BigDecimal) {
        _uiState.value = _uiState.value.copy(
            categorySliders = _uiState.value.categorySliders.map {
                if (it.category.id == category.id) it.copy(limit = amount) else it
            }
        )
        viewModelScope.launch {
            val yearMonth = YearMonth.now().toString()
            saveBudgetUseCase(categoryId = category.id, amount = amount, yearMonth = yearMonth)
        }
    }

    fun recalculateTotalBudget() {
        val sum = _uiState.value.categorySliders
            .map { it.limit }
            .fold(BigDecimal.ZERO) { acc, limit -> acc + limit }
        setTotalBudget(sum)
    }
}
