package com.financetracker.ui.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.financetracker.domain.model.Period
import com.financetracker.ui.components.charts.BarData
import com.financetracker.ui.components.charts.CategoryBreakdownCard
import com.financetracker.ui.components.charts.WeekdayBarChartCard
import com.financetracker.ui.components.core.StatBoxRow
import com.financetracker.ui.components.input.FilterChipGroup
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AnalyticsScreen(modifier: Modifier = Modifier, viewModel: AnalyticsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Period toggle
        FilterChipGroup(
            items = Period.entries,
            selected = { it == uiState.selectedPeriod },
            onSelect = { viewModel.onPeriodSelected(it) },
            label = { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } }
        )

        // Summary stat boxes
        StatBoxRow(
            stats = listOf(
                "Total Spent" to currencyFormatter.format(uiState.totalSpent),
                "Daily Avg" to currencyFormatter.format(uiState.dailyAverage),
                "Count" to uiState.transactionCount.toString()
            )
        )

        // Donut chart
        if (uiState.categorySegments.isNotEmpty()) {
            CategoryBreakdownCard(segments = uiState.categorySegments)
        }

        // Weekday bar chart
        WeekdayBarChartCard(
            bars = uiState.weekdayBars.map { BarData(it.day, it.average) }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}
