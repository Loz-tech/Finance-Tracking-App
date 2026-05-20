package com.financetracker.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.financetracker.ui.components.EmptyState
import com.financetracker.ui.components.TransactionCard
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

    if (!uiState.hasTransactions && !uiState.isLoading) {
        EmptyState(
            icon = "💰",
            title = "No expenses yet",
            subtitle = "Tap + to add your first\nexpense and start tracking",
            modifier = modifier
        )
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Budget summary card
        item(key = "budget") {
            Spacer(modifier = Modifier.height(8.dp))
            BudgetSummaryCard(
                totalSpent = uiState.totalSpent,
                totalBudget = uiState.totalBudget,
                onClick = onNavigateToBudget
            )
        }

        // Category budget progress row
        if (uiState.categoryBudgets.isNotEmpty()) {
            item(key = "category_budgets") {
                Spacer(modifier = Modifier.height(4.dp))
                CategoryBudgetIndicatorRow(budgets = uiState.categoryBudgets)
            }
        }

        // Donut chart + legend
        if (uiState.categorySegments.isNotEmpty()) {
            item(key = "donut") {
                CategoryBreakdownCard(
                    segments = uiState.categorySegments,
                    title = "This Month"
                )
            }
        }

        // Recent activity header
        item(key = "recent_header") {
            Row(
                modifier = Modifier.fillMaxWidth(),
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

        // Recent transactions
        items(
            items = uiState.recentTransactions,
            key = { it.id }
        ) { transaction ->
            TransactionCard(
                transaction = transaction,
                useCard = false,
                iconSize = 44.dp,
                showDate = true,
                verticalPadding = 8.dp,
                onClick = { onEditTransaction(transaction.id) }
            )
        }

        item(key = "bottom_spacer") {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
