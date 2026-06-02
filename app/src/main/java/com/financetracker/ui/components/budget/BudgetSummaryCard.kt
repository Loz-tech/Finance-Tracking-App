package com.financetracker.ui.components.budget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.financetracker.R
import com.financetracker.ui.components.charts.CircularProgressLabel
import com.financetracker.ui.components.core.SectionCard
import com.financetracker.ui.theme.FinanceTrackingAppTheme
import com.financetracker.util.rememberCurrencyFormatter
import java.math.BigDecimal

@Composable
fun BudgetSummaryCard(
    totalSpent: BigDecimal,
    totalBudget: BigDecimal?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val remaining = totalBudget?.subtract(totalSpent) ?: BigDecimal.ZERO
    val progress = if (totalBudget != null && totalBudget > BigDecimal.ZERO) {
        (totalSpent.toFloat() / totalBudget.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }
    val currencyFormatter = rememberCurrencyFormatter()

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
                subText = stringResource(R.string.budget_spent),
                size = 100.dp,
                strokeWidth = 8.dp
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (totalBudget != null) {
                    Text(
                        text = "${currencyFormatter.format(remaining)} ${stringResource(R.string.budget_left)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isOverBudget) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
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
