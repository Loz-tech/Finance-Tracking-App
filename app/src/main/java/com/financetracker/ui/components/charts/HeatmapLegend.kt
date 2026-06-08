package com.financetracker.ui.components.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.financetracker.R
import com.financetracker.ui.theme.FinanceTrackingAppTheme

private val DEFAULT_HEAT_COLORS = listOf(
    Color(0xFFE0E0E0),
    Color(0xFFB2DFDB),
    Color(0xFF80CBC4),
    Color(0xFF4DB6AC),
    Color(0xFF00897B)
)

@Composable
fun HeatmapLegend(modifier: Modifier = Modifier, colors: List<Color> = DEFAULT_HEAT_COLORS) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(stringResource(R.string.heatmap_less), style = MaterialTheme.typography.labelSmall)
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .padding(2.dp)
                    .background(color, RoundedCornerShape(2.dp))
            )
        }
        Text(stringResource(R.string.heatmap_more), style = MaterialTheme.typography.labelSmall)
    }
}

@Preview
@Composable
private fun HeatmapLegendPreview() {
    FinanceTrackingAppTheme {
        HeatmapLegend()
    }
}
