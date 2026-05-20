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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.financetracker.ui.preview.PreviewData
import com.financetracker.ui.theme.FinanceTrackingAppTheme

@Composable
fun CategoryBreakdownCard(
    segments: List<DonutSegment>,
    modifier: Modifier = Modifier,
    title: String = "Category Breakdown"
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            DonutChart(segments)
            Spacer(modifier = Modifier.height(8.dp))
            DonutLegend(segments)
        }
    }
}

@Preview
@Composable
private fun CategoryBreakdownCardPreview() {
    FinanceTrackingAppTheme {
        CategoryBreakdownCard(segments = PreviewData.donutSegments)
    }
}
