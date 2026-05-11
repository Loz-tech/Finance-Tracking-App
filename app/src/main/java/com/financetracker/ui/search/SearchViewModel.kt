package com.financetracker.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.financetracker.domain.model.Category
import com.financetracker.domain.model.Transaction
import com.financetracker.domain.repository.CategoryRepository
import com.financetracker.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
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
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

enum class QuickChip(val label: String) {
    TODAY("Today"),
    LAST_7_DAYS("Last 7 Days"),
    LAST_30_DAYS("Last 30 Days"),
    THIS_MONTH("This Month"),
    THIS_YEAR("This Year");

    fun calculateRange(): Pair<LocalDate, LocalDate> {
        val today = LocalDate.now()
        return when (this) {
            TODAY -> today to today
            LAST_7_DAYS -> today.minusDays(6) to today
            LAST_30_DAYS -> today.minusDays(29) to today
            THIS_MONTH -> today.withDayOfMonth(1) to today
            THIS_YEAR -> today.withDayOfYear(1) to today
        }
    }
}

sealed class DateFilter {
    data object None : DateFilter()
    data class Quick(val chip: QuickChip) : DateFilter()
    data class Custom(val start: LocalDate, val end: LocalDate) : DateFilter()
}

data class SearchUiState(
    val query: String = "",
    val allCategories: List<Category> = emptyList(),
    val selectedCategoryIds: Set<UUID> = emptySet(),
    val results: List<Transaction> = emptyList(),
    val isLoading: Boolean = false,
    val dateFilter: DateFilter = DateFilter.Quick(QuickChip.LAST_7_DAYS)
)

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _selectedCategoryIds = MutableStateFlow<Set<UUID>>(emptySet())
    private val _dateFilter = MutableStateFlow<DateFilter>(DateFilter.Quick(QuickChip.LAST_7_DAYS))
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
                .combine(_dateFilter) { (query, categoryIds), dateFilter ->
                    Triple(query, categoryIds, dateFilter)
                }
                .flatMapLatest { (effectiveQuery, categoryIds, dateFilter) ->
                    if (effectiveQuery.isEmpty() && categoryIds.isEmpty() && dateFilter is DateFilter.None) {
                        flowOf(emptyList())
                    } else {
                        val (start, end) = when (dateFilter) {
                            is DateFilter.None -> null to null
                            is DateFilter.Quick -> {
                                val range = dateFilter.chip.calculateRange()
                                range.first to range.second
                            }
                            is DateFilter.Custom -> dateFilter.start to dateFilter.end
                        }
                        transactionRepository.searchTransactions(
                            query = effectiveQuery,
                            categoryIds = categoryIds.toList(),
                            start = start,
                            end = end
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

    fun onQuickChipSelected(chip: QuickChip) {
        _dateFilter.value = DateFilter.Quick(chip)
        _uiState.update { it.copy(dateFilter = DateFilter.Quick(chip)) }
    }

    fun onCustomDateRangeSelected(start: LocalDate, end: LocalDate) {
        _dateFilter.value = DateFilter.Custom(start, end)
        _uiState.update { it.copy(dateFilter = DateFilter.Custom(start, end)) }
    }

    fun clearDateFilter() {
        _dateFilter.value = DateFilter.None
        _uiState.update { it.copy(dateFilter = DateFilter.None) }
    }

    fun clearSearch() {
        _query.value = ""
        _selectedCategoryIds.value = emptySet()
        _dateFilter.value = DateFilter.Quick(QuickChip.LAST_7_DAYS)
        _uiState.value = SearchUiState(
            allCategories = _uiState.value.allCategories
        )
    }
}
