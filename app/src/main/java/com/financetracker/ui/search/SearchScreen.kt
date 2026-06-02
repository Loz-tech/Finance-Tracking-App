package com.financetracker.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.financetracker.domain.model.DateFilter
import com.financetracker.domain.model.IconStyle
import com.financetracker.domain.model.QuickChip
import com.financetracker.ui.components.core.EmptyState
import com.financetracker.ui.components.core.TransactionCard
import com.financetracker.ui.components.input.DateFilterChipRow
import com.financetracker.ui.components.input.DateRangePicker
import com.financetracker.ui.components.input.FilterChipGroup
import com.financetracker.ui.components.input.SearchTextField
import java.time.format.DateTimeFormatter
import java.util.UUID

@Composable
fun SearchScreen(
    onEditTransaction: (UUID) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchFocusRequester = remember { FocusRequester() }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM d") }

    var showDateRangePicker by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        SearchTextField(
            query = uiState.query,
            onQueryChange = viewModel::onQueryChanged,
            onClear = viewModel::clearSearch,
            modifier = Modifier.fillMaxWidth(),
            focusRequester = searchFocusRequester
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Date range",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))

        val allChips = QuickChip.entries.toList() + "Custom"
        DateFilterChipRow(
            items = allChips,
            selected = { item ->
                when (item) {
                    is QuickChip ->
                        uiState.dateFilter is DateFilter.Quick &&
                            (uiState.dateFilter as DateFilter.Quick).chip == item
                    "Custom" -> uiState.dateFilter is DateFilter.Custom
                    else -> false
                }
            },
            onSelect = { item ->
                when (item) {
                    is QuickChip -> viewModel.onQuickChipSelected(item)
                    "Custom" -> {
                        if (uiState.dateFilter !is DateFilter.Custom) {
                            showDateRangePicker = true
                        }
                    }
                }
            },
            label = { item ->
                when (item) {
                    is QuickChip -> item.label
                    "Custom" -> when (val df = uiState.dateFilter) {
                        is DateFilter.Custom -> "${df.start.format(
                            dateFormatter
                        )} \u2013 ${df.end.format(dateFormatter)}"
                        else -> "Custom"
                    }
                    else -> item.toString()
                }
            },
            showClear = uiState.dateFilter != DateFilter.None,
            onClear = viewModel::clearDateFilter
        )

        if (uiState.allCategories.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            FilterChipGroup(
                items = uiState.allCategories,
                selected = { it.id in uiState.selectedCategoryIds },
                onSelect = { viewModel.onCategoryToggled(it.id) },
                label = { category -> category.name }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val hasActiveFilter = uiState.query.isNotEmpty() ||
            uiState.selectedCategoryIds.isNotEmpty() ||
            uiState.dateFilter !is DateFilter.None

        if (uiState.results.isEmpty() && hasActiveFilter) {
            EmptyState(
                icon = "\ud83d\udd0d",
                title = "No transactions found",
                subtitle = "Try adjusting your filters"
            )
        } else if (uiState.results.isEmpty() && !hasActiveFilter) {
            EmptyState(
                icon = "\ud83d\udd0d",
                title = "Select a filter to search",
                subtitle = "Use the search bar or pick a filter above"
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(uiState.results, key = { it.id }) { transaction ->
                    TransactionCard(
                        transaction = transaction,
                        iconStyle = IconStyle.FILLED,
                        iconSize = 36.dp,
                        cardCornerRadius = 8.dp,
                        onClick = { onEditTransaction(transaction.id) }
                    )
                }
            }
        }
    }

    if (showDateRangePicker) {
        DateRangePicker(
            onRangeSelected = { start, end ->
                viewModel.onCustomDateRangeSelected(start, end)
                showDateRangePicker = false
            },
            onDismiss = { showDateRangePicker = false }
        )
    }
}
