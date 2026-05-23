package com.financetracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.financetracker.ui.theme.FinanceTrackingAppTheme

@Composable
fun WeekdayBarChartCard(bars: List<BarData>, modifier: Modifier = Modifier, title: String = "Weekday Averages") {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            BarChart(bars = bars)
        }
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
