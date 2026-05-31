package com.financetracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.financetracker.ui.theme.FinanceTrackingAppTheme
import java.math.BigDecimal
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale

@Composable
fun BudgetSummaryCard(
    totalSpent: BigDecimal,
    totalBudget: BigDecimal?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val daysInMonth = today.lengthOfMonth()
    val daysLeft = daysInMonth - today.dayOfMonth
    val remaining = totalBudget?.subtract(totalSpent) ?: BigDecimal.ZERO
    val progress = if (totalBudget != null && totalBudget > BigDecimal.ZERO) {
        (totalSpent.toFloat() / totalBudget.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())

    val isOverBudget = remaining < BigDecimal.ZERO
    SectionCard(
        modifier = modifier.clickable { onClick() },
        containerColor = if (isOverBudget) {
            MaterialTheme.colorScheme.errorContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerLow
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressLabel(
                progress = progress,
                centerText = currencyFormatter.format(totalSpent),
                subText = "spent",
                size = 100.dp,
                strokeWidth = 8.dp
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Monthly Budget",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (totalBudget != null) {
                    Text(
                        text = "${currencyFormatter.format(remaining)} left",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isOverBudget) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
                Text(
                    text = "$daysLeft days remaining",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Preview
@Composable
private fun BudgetSummaryCardPreview() {
    FinanceTrackingAppTheme {
        BudgetSummaryCard(
            totalSpent = BigDecimal("340.00"),
            totalBudget = BigDecimal("500.00"),
            onClick = {}
        )
    }
}
