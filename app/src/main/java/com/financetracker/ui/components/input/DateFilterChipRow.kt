package com.financetracker.ui.components.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.financetracker.ui.theme.FinanceTrackingAppTheme

@Composable
fun <T> DateFilterChipRow(
    items: List<T>,
    selected: (T) -> Boolean,
    onSelect: (T) -> Unit,
    label: (T) -> String,
    showClear: Boolean,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChipGroup(
            items = items,
            selected = selected,
            onSelect = onSelect,
            label = label,
            modifier = Modifier.weight(1f)
        )
        if (showClear) {
            IconButton(onClick = onClear) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear date filter"
                )
            }
        }
    }
}

@Preview
@Composable
private fun DateFilterChipRowPreview() {
    FinanceTrackingAppTheme {
        DateFilterChipRow(
            items = listOf("Today", "Week", "Month", "Custom"),
            selected = { it == "Week" },
            onSelect = {},
            label = { it },
            showClear = true,
            onClear = {}
        )
    }
}

@Preview
@Composable
private fun DateFilterChipRowNoClearPreview() {
    FinanceTrackingAppTheme {
        DateFilterChipRow(
            items = listOf("Today", "Week", "Month"),
            selected = { false },
            onSelect = {},
            label = { it },
            showClear = false,
            onClear = {}
        )
    }
}
