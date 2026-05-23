package com.financetracker.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.financetracker.ui.components.EmptyState
import com.financetracker.ui.components.MonthNavigator
import com.financetracker.ui.components.TransactionCard
import com.financetracker.ui.components.rememberIconStyle
import java.util.UUID

@Composable
fun HistoryScreen(
    onEditTransaction: (UUID) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val iconStyle = rememberIconStyle()

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
                    Text(
                        text = group.label,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(vertical = 8.dp)
                    )
                }

                items(
                    items = group.transactions,
                    key = { it.id }
                ) { transaction ->
                    TransactionCard(
                        transaction = transaction,
                        iconStyle = iconStyle,
                        onClick = { onEditTransaction(transaction.id) },
                        onDelete = { viewModel.deleteTransaction(transaction) }
                    )
                }
            }

            if (uiState.dateGroups.isEmpty() && !uiState.isLoading) {
                item {
                    EmptyState(
                        icon = "📜",
                        title = "No transactions this month",
                        subtitle = "Add some expenses to see them here"
                    )
                }
            }
        }
    }
}
