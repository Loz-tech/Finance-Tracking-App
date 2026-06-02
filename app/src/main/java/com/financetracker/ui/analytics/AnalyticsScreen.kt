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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.financetracker.R
import com.financetracker.domain.model.Period
import com.financetracker.ui.components.charts.BarData
import com.financetracker.ui.components.charts.CategoryBreakdownCard
import com.financetracker.ui.components.charts.WeekdayBarChartCard
import com.financetracker.ui.components.core.StatBoxRow
import com.financetracker.ui.components.input.FilterChipGroup
import com.financetracker.util.rememberCurrencyFormatter

@Composable
fun AnalyticsScreen(modifier: Modifier = Modifier, viewModel: AnalyticsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val currencyFormatter = rememberCurrencyFormatter()

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
            label = {
                when (it) {
                    Period.WEEK -> stringResource(R.string.period_week)
                    Period.MONTH -> stringResource(R.string.period_month)
                    Period.YEAR -> stringResource(R.string.period_year)
                }
            }
        )

        // Summary stat boxes
        StatBoxRow(
            stats = listOf(
                stringResource(R.string.analytics_total_spent) to currencyFormatter.format(uiState.totalSpent),
                stringResource(R.string.analytics_daily_avg) to currencyFormatter.format(uiState.dailyAverage),
                stringResource(R.string.analytics_count) to uiState.transactionCount.toString()
            )
        )

        // Donut chart
        if (uiState.categorySegments.isNotEmpty()) {
            CategoryBreakdownCard(
                segments = uiState.categorySegments,
                title = stringResource(R.string.analytics_categories)
            )
        }

        // Weekday bar chart
        WeekdayBarChartCard(
            bars = uiState.weekdayBars.map { BarData(it.day, it.average) },
            title = stringResource(R.string.analytics_weekday_averages)
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}
