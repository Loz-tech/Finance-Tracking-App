package com.financetracker.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.financetracker.domain.model.Category
import com.financetracker.domain.model.DateFilter
import com.financetracker.domain.model.QuickChip
import com.financetracker.domain.model.SearchCriteria
import com.financetracker.domain.model.Transaction
import com.financetracker.domain.repository.CategoryRepository
import com.financetracker.domain.repository.TransactionRepository
import com.financetracker.domain.usecase.SearchTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val allCategories: List<Category> = emptyList(),
    val selectedCategoryIds: Set<UUID> = emptySet(),
    val results: List<Transaction> = emptyList(),
    val isLoading: Boolean = false,
    val dateFilter: DateFilter = DateFilter.Quick(QuickChip.LAST_7_DAYS)
)

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchTransactionsUseCase: SearchTransactionsUseCase,
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val queryFlow = MutableStateFlow("")
    private val selectedCategoryIdsFlow = MutableStateFlow<Set<UUID>>(emptySet())
    private val dateFilterFlow = MutableStateFlow<DateFilter>(DateFilter.Quick(QuickChip.LAST_7_DAYS))
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    init {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categories ->
                _uiState.update { it.copy(allCategories = categories) }
            }
        }

        val criteria = combine(
            queryFlow,
            selectedCategoryIdsFlow,
            dateFilterFlow
        ) { q, cats, df ->
            SearchCriteria(query = q, categoryIds = cats, dateFilter = df)
        }.debounce(300)

        searchTransactionsUseCase(criteria)
            .onEach { results ->
                _uiState.update { it.copy(results = results, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChanged(query: String) {
        queryFlow.value = query
        _uiState.update { it.copy(query = query) }
    }

    fun onCategoryToggled(categoryId: UUID) {
        val current = selectedCategoryIdsFlow.value.toMutableSet()
        if (categoryId in current) {
            current.remove(categoryId)
        } else {
            current.add(categoryId)
        }
        selectedCategoryIdsFlow.value = current
        _uiState.update { it.copy(selectedCategoryIds = current) }
    }

    fun onQuickChipSelected(chip: QuickChip) {
        val filter = DateFilter.Quick(chip)
        dateFilterFlow.value = filter
        _uiState.update { it.copy(dateFilter = filter) }
    }

    fun onCustomDateRangeSelected(start: java.time.LocalDate, end: java.time.LocalDate) {
        val filter = DateFilter.Custom(start, end)
        dateFilterFlow.value = filter
        _uiState.update { it.copy(dateFilter = filter) }
    }

    fun clearDateFilter() {
        dateFilterFlow.value = DateFilter.None
        _uiState.update { it.copy(dateFilter = DateFilter.None) }
    }

    fun clearSearch() {
        queryFlow.value = ""
        selectedCategoryIdsFlow.value = emptySet()
        dateFilterFlow.value = DateFilter.Quick(QuickChip.LAST_7_DAYS)
        _uiState.value = SearchUiState(allCategories = _uiState.value.allCategories)
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transaction)
        }
    }
}
