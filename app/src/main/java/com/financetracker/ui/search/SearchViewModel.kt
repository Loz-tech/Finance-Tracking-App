package com.financetracker.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.financetracker.domain.model.Category
import com.financetracker.domain.model.Transaction
import com.financetracker.domain.repository.CategoryRepository
import com.financetracker.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val allCategories: List<Category> = emptyList(),
    val selectedCategoryIds: Set<UUID> = emptySet(),
    val results: List<Transaction> = emptyList(),
    val isLoading: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _selectedCategoryIds = MutableStateFlow<Set<UUID>>(emptySet())
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    init {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categories ->
                _uiState.value = _uiState.value.copy(allCategories = categories)
            }
        }

        viewModelScope.launch {
            _query.combine(_selectedCategoryIds) { query, categoryIds ->
                val effectiveQuery = query.trim()
                if (effectiveQuery.isEmpty() && categoryIds.isEmpty()) {
                    emptyList()
                } else if (effectiveQuery.isEmpty()) {
                    // Filter only by categories
                    categoryIds.flatMap { id ->
                        transactionRepository.getTransactionsByCategory(id).let { flow ->
                            var result = emptyList<Transaction>()
                            flow.collect { result = it }
                            result
                        }
                    }
                } else {
                    val searchResults = transactionRepository.searchTransactions(effectiveQuery).let { flow ->
                        var result = emptyList<Transaction>()
                        flow.collect { result = it }
                        result
                    }
                    if (categoryIds.isEmpty()) searchResults
                    else searchResults.filter { it.category.id in categoryIds }
                }
            }.collect { results ->
                _uiState.value = _uiState.value.copy(results = results)
            }
        }
    }

    fun onQueryChanged(query: String) {
        _query.value = query
        _uiState.value = _uiState.value.copy(query = query)
    }

    fun onCategoryToggled(categoryId: UUID) {
        val current = _selectedCategoryIds.value.toMutableSet()
        if (categoryId in current) current.remove(categoryId)
        else current.add(categoryId)
        _selectedCategoryIds.value = current
        _uiState.value = _uiState.value.copy(selectedCategoryIds = current)
    }

    fun clearSearch() {
        _query.value = ""
        _selectedCategoryIds.value = emptySet()
        _uiState.value = _uiState.value.copy(query = "", selectedCategoryIds = emptySet(), results = emptyList())
    }
}
