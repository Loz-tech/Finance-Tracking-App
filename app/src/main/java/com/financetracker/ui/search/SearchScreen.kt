package com.financetracker.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import com.financetracker.ui.components.TransactionCard
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onEditTransaction: (java.util.UUID) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var pendingCustomStart by remember { mutableStateOf<LocalDate?>(null) }

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
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                QuickChip.entries.forEach { chip ->
                    val dateFilter = uiState.dateFilter
                    val selected = dateFilter is DateFilter.Quick && dateFilter.chip == chip
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.onQuickChipSelected(chip) },
                        label = { Text(chip.label, style = MaterialTheme.typography.labelMedium) }
                    )
                }

                // Custom chip
                val isCustomSelected = uiState.dateFilter is DateFilter.Custom
                val customLabel = when (val df = uiState.dateFilter) {
                    is DateFilter.Custom -> "${df.start.format(dateFormatter)} – ${df.end.format(dateFormatter)}"
                    else -> "Custom"
                }
                FilterChip(
                    selected = isCustomSelected,
                    onClick = {
                        if (!isCustomSelected) {
                            showStartDatePicker = true
                        }
                    },
                    label = { Text(customLabel, style = MaterialTheme.typography.labelMedium) }
                )
            }

            if (uiState.dateFilter != DateFilter.None) {
                IconButton(onClick = viewModel::clearDateFilter) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear date filter")
                }
            }
        }

        // Category filter chips
        if (uiState.allCategories.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                uiState.allCategories.forEach { category ->
                    FilterChip(
                        selected = category.id in uiState.selectedCategoryIds,
                        onClick = { viewModel.onCategoryToggled(category.id) },
                        label = {
                            Text("${category.emoji} ${category.name}", style = MaterialTheme.typography.labelMedium)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Results
        val hasActiveFilter = uiState.query.isNotEmpty() ||
                uiState.selectedCategoryIds.isNotEmpty() ||
                uiState.dateFilter !is DateFilter.None

        if (uiState.results.isEmpty() && hasActiveFilter) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 64.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No transactions found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else if (uiState.results.isEmpty() && !hasActiveFilter) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 64.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Select a filter to search",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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

    // Start date picker
    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        pendingCustomStart = date
                        showStartDatePicker = false
                        showEndDatePicker = true
                    } ?: run {
                        showStartDatePicker = false
                    }
                }) { Text("Next") }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // End date picker
    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val endDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        pendingCustomStart?.let { startDate ->
                            viewModel.onCustomDateRangeSelected(startDate, endDate)
                        }
                        showEndDatePicker = false
                        pendingCustomStart = null
                    } ?: run {
                        showEndDatePicker = false
                        pendingCustomStart = null
                    }
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showEndDatePicker = false
                    pendingCustomStart = null
                }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

// End of file
