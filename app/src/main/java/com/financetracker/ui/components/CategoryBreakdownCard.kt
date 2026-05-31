package com.financetracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.financetracker.ui.preview.PreviewData
import com.financetracker.ui.theme.FinanceTrackingAppTheme

@Composable
fun CategoryBreakdownCard(segments: List<DonutSegment>, modifier: Modifier = Modifier, title: String = "Categories") {
    SectionCard(modifier = modifier.heightIn(min = 280.dp), title = title) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 248.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DonutChart(segments, modifier = Modifier.size(160.dp))
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DonutLegend(segments)
                }
            }
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
