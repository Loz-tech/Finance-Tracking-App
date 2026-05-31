package com.financetracker.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.financetracker.ui.components.BudgetSummaryCard
import com.financetracker.ui.components.CategoryBreakdownCard
import com.financetracker.ui.components.CategoryBudgetIndicatorRow
import com.financetracker.ui.components.DonutSegment
import com.financetracker.ui.components.EmptyState
import com.financetracker.ui.components.TransactionCard
import com.financetracker.ui.theme.ChartColors
import java.util.UUID

@Composable
fun HomeScreen(
    onAddTransaction: () -> Unit,
    onEditTransaction: (UUID) -> Unit,
    onNavigateToBudget: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val iconStyle = com.financetracker.ui.components.rememberIconStyle()

    if (!uiState.hasTransactions && !uiState.isLoading) {
        EmptyState(
            icon = "💰",
            title = "No expenses yet",
            subtitle = "Tap + to add your first\nexpense and start tracking",
            modifier = modifier
        )
        return
    }

    val segments = uiState.categoryBreakdowns.mapIndexed { i, bd ->
        DonutSegment(
            label = bd.name,
            iconName = bd.iconName,
            value = bd.amount.toDouble().toFloat(),
            color = ChartColors[i % ChartColors.size]
        )
    }
    val recentColor = MaterialTheme.colorScheme.surfaceContainerLow

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item(key = "budget") {
            Spacer(modifier = Modifier.height(8.dp))
            BudgetSummaryCard(
                totalSpent = uiState.totalSpent,
                totalBudget = uiState.totalBudget,
                onClick = onNavigateToBudget
            )
        }

        if (uiState.categoryBudgets.isNotEmpty()) {
            item(key = "spacer_cat") { Spacer(modifier = Modifier.height(20.dp)) }
            item(key = "category_budgets") {
                CategoryBudgetIndicatorRow(budgets = uiState.categoryBudgets, iconStyle = iconStyle)
            }
        }

        if (segments.isNotEmpty()) {
            item(key = "spacer_donut") { Spacer(modifier = Modifier.height(16.dp)) }
            item(key = "donut") {
                CategoryBreakdownCard(
                    segments = segments,
                    title = "This Month"
                )
            }
        }

        item(key = "spacer_recent") { Spacer(modifier = Modifier.height(16.dp)) }

        item(key = "recent_header") {
            val headerShape = if (uiState.recentTransactions.isEmpty()) {
                RoundedCornerShape(12.dp)
            } else {
                RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(recentColor, shape = headerShape)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Activity",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "History",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        itemsIndexed(
            items = uiState.recentTransactions,
            key = { _, tx -> tx.id }
        ) { index, transaction ->
            val isLast = index == uiState.recentTransactions.lastIndex
            val itemShape = if (isLast) {
                RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
            } else {
                RoundedCornerShape(0.dp)
            }
            TransactionCard(
                transaction = transaction,
                iconStyle = iconStyle,
                modifier = Modifier.background(recentColor, shape = itemShape),
                useCard = false,
                iconSize = 44.dp,
                showDate = true,
                horizontalPadding = 16.dp,
                verticalPadding = 8.dp,
                onClick = { onEditTransaction(transaction.id) }
            )
        }

        item(key = "bottom_spacer") {
            Spacer(modifier = Modifier.height(96.dp))
        }
    }
}
