package com.financetracker.ui.components.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.financetracker.ui.theme.FinanceTrackingAppTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> FilterChipGroup(
    items: List<T>,
    selected: (T) -> Boolean,
    onSelect: (T) -> Unit,
    label: @Composable (T) -> String,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items.forEach { item ->
            FilterChip(
                selected = selected(item),
                onClick = { onSelect(item) },
                label = { Text(label(item), style = MaterialTheme.typography.labelMedium) }
            )
        }
    }
}

@Preview
@Composable
private fun FilterChipGroupPreview() {
    FinanceTrackingAppTheme {
        val items = listOf("Today", "Week", "Month", "Year")
        var selected = "Month"
        FilterChipGroup(
            items = items,
            selected = { it == selected },
            onSelect = { selected = it },
            label = { it }
        )
    }
}
