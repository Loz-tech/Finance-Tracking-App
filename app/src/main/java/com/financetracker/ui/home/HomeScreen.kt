package com.financetracker.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.financetracker.R
import com.financetracker.ui.components.budget.BudgetSummaryCard
import com.financetracker.ui.components.category.CategoryBudgetIndicatorRow
import com.financetracker.ui.components.charts.CategoryBreakdownCard
import com.financetracker.ui.components.charts.DonutSegment
import com.financetracker.ui.components.core.EmptyState
import com.financetracker.ui.components.core.RecentActivityHeader
import com.financetracker.ui.components.core.SwipeableTransactionCard
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
    val iconStyle = com.financetracker.ui.components.util.rememberIconStyle()
    var currentlySwipedId by remember { mutableStateOf<UUID?>(null) }

    if (!uiState.hasTransactions && !uiState.isLoading) {
        EmptyState(
            icon = "💰",
            title = stringResource(R.string.home_empty_title),
            subtitle = stringResource(R.string.home_empty_subtitle),
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
                    title = stringResource(R.string.home_this_month)
                )
            }
        }

        item(key = "spacer_recent") { Spacer(modifier = Modifier.height(16.dp)) }

        item(key = "recent_header") {
            RecentActivityHeader(
                title = stringResource(R.string.home_recent_activity),
                actionLabel = stringResource(R.string.home_history),
                isEmpty = uiState.recentTransactions.isEmpty()
            )
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
            SwipeableTransactionCard(
                transaction = transaction,
                iconStyle = iconStyle,
                onEdit = { onEditTransaction(transaction.id) },
                onDelete = { viewModel.deleteTransaction(transaction) },
                currentlySwipedId = currentlySwipedId,
                onSwipeOpened = { currentlySwipedId = it },
                modifier = Modifier.background(recentColor, shape = itemShape),
                shape = itemShape,
                useCard = false,
                iconSize = 44.dp,
                showDate = true,
                horizontalPadding = 16.dp,
                verticalPadding = 8.dp
            )
        }

        item(key = "bottom_spacer") {
            Spacer(modifier = Modifier.height(96.dp))
        }
    }
}
