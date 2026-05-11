package com.financetracker.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.financetracker.domain.model.Category
import com.financetracker.domain.model.Transaction
import com.financetracker.domain.repository.CategoryRepository
import com.financetracker.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
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

@OptIn(ExperimentalCoroutinesApi::class, kotlinx.coroutines.FlowPreview::class)
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
                _uiState.update { it.copy(allCategories = categories) }
            }
        }

        val searchResults: Flow<List<Transaction>> =
            _query
                .debounce(300)
                .combine(_selectedCategoryIds) { query, categoryIds ->
                    query.trim() to categoryIds
                }
                .flatMapLatest { (effectiveQuery, categoryIds) ->
                    if (effectiveQuery.isEmpty() && categoryIds.isEmpty()) {
                        flowOf(emptyList())
                    } else {
                        transactionRepository.searchTransactions(
                            query = effectiveQuery,
                            categoryIds = categoryIds.toList()
                        )
                    }
                }

        searchResults
            .onEach { results ->
                _uiState.update { it.copy(results = results) }
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChanged(query: String) {
        _query.value = query
        _uiState.update { it.copy(query = query) }
    }

    fun onCategoryToggled(categoryId: UUID) {
        val current = _selectedCategoryIds.value.toMutableSet()
        if (categoryId in current) current.remove(categoryId)
        else current.add(categoryId)
        _selectedCategoryIds.value = current
        _uiState.update { it.copy(selectedCategoryIds = current) }
    }

    fun clearSearch() {
        _query.value = ""
        _selectedCategoryIds.value = emptySet()
        _uiState.value = SearchUiState(
            allCategories = _uiState.value.allCategories
        )
    }
}
