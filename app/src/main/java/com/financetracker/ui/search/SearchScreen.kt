package com.financetracker.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.financetracker.ui.components.DateRangePicker
import com.financetracker.ui.components.EmptyState
import com.financetracker.ui.components.FilterChipGroup
import com.financetracker.ui.components.TransactionCard
import java.time.format.DateTimeFormatter
import java.util.UUID

@Composable
fun SearchScreen(
    onEditTransaction: (UUID) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    var showDateRangePicker by remember { mutableStateOf(false) }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM d") }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        // Search input
        OutlinedTextField(
            value = uiState.query,
            onValueChange = viewModel::onQueryChanged,
            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
            placeholder = { Text("Search expenses...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (uiState.query.isNotEmpty()) {
                    IconButton(onClick = viewModel::clearSearch) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Date range label
        Text(
            text = "Date range",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))

        // Date filter chips + clear button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val allChips = QuickChip.entries.toList() + "Custom"
            FilterChipGroup(
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
                            )} – ${df.end.format(dateFormatter)}"
                            else -> "Custom"
                        }
                        else -> item.toString()
                    }
                },
                modifier = Modifier.weight(1f)
            )

            if (uiState.dateFilter != DateFilter.None) {
                IconButton(onClick = viewModel::clearDateFilter) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear date filter")
                }
            }
        }

        // Category filter chips
        if (uiState.allCategories.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            FilterChipGroup(
                items = uiState.allCategories,
                selected = { it.id in uiState.selectedCategoryIds },
                onSelect = { viewModel.onCategoryToggled(it.id) },
                label = { "${it.emoji} ${it.name}" }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Results
        val hasActiveFilter = uiState.query.isNotEmpty() ||
            uiState.selectedCategoryIds.isNotEmpty() ||
            uiState.dateFilter !is DateFilter.None

        if (uiState.results.isEmpty() && hasActiveFilter) {
            EmptyState(
                icon = "🔍",
                title = "No transactions found",
                subtitle = "Try adjusting your filters"
            )
        } else if (uiState.results.isEmpty() && !hasActiveFilter) {
            EmptyState(
                icon = "🔍",
                title = "Select a filter to search",
                subtitle = "Use the search bar or pick a filter above"
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(uiState.results, key = { it.id }) { transaction ->
                    TransactionCard(
                        transaction = transaction,
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
