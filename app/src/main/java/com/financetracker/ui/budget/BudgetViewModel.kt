package com.financetracker.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.financetracker.domain.model.Budget
import com.financetracker.domain.model.Category
import com.financetracker.domain.repository.BudgetRepository
import com.financetracker.domain.repository.CategoryRepository
import com.financetracker.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import java.time.YearMonth
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class CategoryBudgetSliders(val category: Category, val limit: BigDecimal, val spent: BigDecimal)

data class BudgetUiState(
    val totalBudget: BigDecimal = BigDecimal.ZERO,
    val categorySliders: List<CategoryBudgetSliders> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState: StateFlow<BudgetUiState> = _uiState

    init {
        loadBudget()
    }

    private fun loadBudget() {
        viewModelScope.launch {
            val yearMonth = YearMonth.now().toString()
            budgetRepository.deleteDuplicateBudgets()
            val totalBudget = budgetRepository.getTotalBudget(yearMonth)?.limitAmount ?: BigDecimal.ZERO
            val categories = categoryRepository.getAllCategories().first()
            val existingBudgets = budgetRepository.getBudgetsByYearMonth(yearMonth).first()
            val prevBudgets = budgetRepository.getBudgetsByYearMonth(YearMonth.now().minusMonths(1).toString()).first()

            val sliders = categories.map { cat ->
                val existing = existingBudgets.find { it.categoryId == cat.id }
                val prev = prevBudgets.find { it.categoryId == cat.id }
                val limit = existing?.limitAmount ?: prev?.limitAmount ?: BigDecimal.ZERO
                CategoryBudgetSliders(cat, limit, BigDecimal.ZERO)
            }

            _uiState.value = BudgetUiState(totalBudget, sliders, false)
        }
    }

    fun setTotalBudget(amount: BigDecimal) {
        _uiState.value = _uiState.value.copy(totalBudget = amount)
        viewModelScope.launch {
            val yearMonth = YearMonth.now().toString()
            val existing = budgetRepository.getTotalBudget(yearMonth)
            val prefs = settingsRepository.userPreferences.first()
            val budget = Budget(
                id = existing?.id ?: UUID.randomUUID(),
                categoryId = null,
                yearMonth = yearMonth,
                limitAmount = amount,
                originalLimitAmount = existing?.originalLimitAmount ?: amount,
                originalCurrencyCode = existing?.originalCurrencyCode ?: prefs.currencyCode
            )
            budgetRepository.saveBudget(budget)
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
            val existing = budgetRepository.getCategoryBudget(yearMonth, category.id)
            val prefs = settingsRepository.userPreferences.first()
            val budget = Budget(
                id = existing?.id ?: UUID.randomUUID(),
                categoryId = category.id,
                yearMonth = yearMonth,
                limitAmount = amount,
                originalLimitAmount = existing?.originalLimitAmount ?: amount,
                originalCurrencyCode = existing?.originalCurrencyCode ?: prefs.currencyCode
            )
            budgetRepository.saveBudget(budget)
        }
    }

    fun recalculateTotalBudget() {
        val sum = _uiState.value.categorySliders
            .map { it.limit }
            .fold(BigDecimal.ZERO) { acc, limit -> acc + limit }
        setTotalBudget(sum)
    }
}
