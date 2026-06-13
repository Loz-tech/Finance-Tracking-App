package com.financetracker.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.financetracker.ui.components.core.DateGroupHeader
import com.financetracker.ui.components.core.EmptyState
import com.financetracker.ui.components.core.SwipeableTransactionCard
import com.financetracker.ui.components.input.MonthNavigator
import com.financetracker.ui.components.util.rememberIconStyle
import java.util.UUID

@Composable
fun HistoryScreen(
    onEditTransaction: (UUID) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val iconStyle = rememberIconStyle()
    var currentlySwipedId by remember { mutableStateOf<UUID?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        MonthNavigator(
            yearMonth = uiState.currentYearMonth,
            onPrevious = viewModel::previousMonth,
            onNext = viewModel::nextMonth
        )

        Spacer(modifier = Modifier.height(4.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            uiState.dateGroups.forEach { group ->
                stickyHeader(key = group.date.toString()) {
                    DateGroupHeader(label = group.label)
                }

                items(
                    items = group.transactions,
                    key = { it.id }
                ) { transaction ->
                    SwipeableTransactionCard(
                        transaction = transaction,
                        iconStyle = iconStyle,
                        onEdit = { onEditTransaction(transaction.id) },
                        onDelete = { viewModel.deleteTransaction(transaction) },
                        currentlySwipedId = currentlySwipedId,
                        onSwipeOpened = { currentlySwipedId = it }
                    )
                }
            }

            if (uiState.dateGroups.isEmpty() && !uiState.isLoading) {
                item {
                    EmptyState(
                        icon = "📜",
                        title = stringResource(R.string.history_empty_title),
                        subtitle = stringResource(R.string.history_empty_subtitle)
                    )
                }
            }
        }
    }
}
