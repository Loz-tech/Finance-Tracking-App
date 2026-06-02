package com.financetracker.ui.components.charts

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.financetracker.ui.components.core.SectionCard
import com.financetracker.ui.theme.FinanceTrackingAppTheme

@Composable
fun WeekdayBarChartCard(bars: List<BarData>, modifier: Modifier = Modifier, title: String = "Weekday Averages") {
    SectionCard(modifier = modifier, title = title) {
        BarChart(bars = bars)
    }
}

@Preview
@Composable
private fun WeekdayBarChartCardPreview() {
    FinanceTrackingAppTheme {
        WeekdayBarChartCard(
            bars = listOf(
                BarData("Mon", 45.0),
                BarData("Tue", 12.5),
                BarData("Wed", 89.0),
                BarData("Thu", 34.0),
                BarData("Fri", 67.5),
                BarData("Sat", 120.0),
                BarData("Sun", 5.0)
            )
        )
    }
}
