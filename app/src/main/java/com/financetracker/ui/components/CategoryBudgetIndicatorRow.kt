package com.financetracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.financetracker.ui.home.CategoryBudgetProgress
import com.financetracker.ui.preview.PreviewData
import com.financetracker.ui.theme.FinanceTrackingAppTheme
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CategoryBudgetIndicatorRow(budgets: List<CategoryBudgetProgress>, modifier: Modifier = Modifier) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(budgets) { budget ->
            val progress = if (budget.limit > BigDecimal.ZERO) {
                (budget.spent.toFloat() / budget.limit.toFloat()).coerceIn(0f, 1f)
            } else {
                0f
            }
            val indicatorColor = budget.colorHex?.let { safeColor ->
                try {
                    Color(android.graphics.Color.parseColor(safeColor))
                } catch (_: IllegalArgumentException) {
                    MaterialTheme.colorScheme.primary
                }
            } ?: MaterialTheme.colorScheme.primary
            val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.size(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth(),
                        strokeWidth = 4.dp,
                        color = indicatorColor,
                        trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                    Text(
                        text = budget.emoji,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = budget.categoryName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Text(
                    text = currencyFormatter.format(budget.spent),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1
                )
            }
        }
    }
}

@Preview
@Composable
private fun CategoryBudgetIndicatorRowPreview() {
    FinanceTrackingAppTheme {
        CategoryBudgetIndicatorRow(budgets = PreviewData.categoryBudgetProgress)
    }
}
