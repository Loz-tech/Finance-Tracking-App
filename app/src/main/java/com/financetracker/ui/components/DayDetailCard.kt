package com.financetracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.financetracker.domain.model.Transaction
import com.financetracker.ui.calendar.CalendarDay
import com.financetracker.ui.preview.PreviewData
import com.financetracker.ui.theme.FinanceTrackingAppTheme
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DayDetailCard(day: CalendarDay, modifier: Modifier = Modifier) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                DateTimeFormatter.ofPattern("EEEE, MMM d").format(day.date),
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                currencyFormatter.format(day.total),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            val txns = day.transactions
            if (txns.isEmpty()) {
                Text(
                    "No transactions",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    txns.forEach { t ->
                        TransactionRowItem(transaction = t, currencyFormatter = currencyFormatter)
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionRowItem(transaction: Transaction, currencyFormatter: NumberFormat) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            transaction.note.ifBlank { transaction.category.name },
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            currencyFormatter.format(transaction.amount),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview
@Composable
private fun DayDetailCardPreview() {
    FinanceTrackingAppTheme {
        DayDetailCard(
            day = CalendarDay(
                date = java.time.LocalDate.now(),
                total = 127.50,
                transactions = PreviewData.transactions.take(2),
                intensity = 2
            )
        )
    }
}
