package com.financetracker.ui.components.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.financetracker.ui.theme.FinanceTrackingAppTheme

private val DEFAULT_WEEKDAYS = listOf("S", "M", "T", "W", "T", "F", "S")

@Composable
fun WeekdayHeader(modifier: Modifier = Modifier, weekdays: List<String> = DEFAULT_WEEKDAYS) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        weekdays.forEach {
            Text(
                text = it,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview
@Composable
private fun WeekdayHeaderPreview() {
    FinanceTrackingAppTheme {
        WeekdayHeader()
    }
}
