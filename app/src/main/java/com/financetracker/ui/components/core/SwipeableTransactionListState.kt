package com.financetracker.ui.components.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.financetracker.domain.model.Transaction
import java.util.UUID

/**
 * Compose state holder that owns the swipe-to-action mutex (only one card open across a list)
 * plus the delete action. Per-screen instance via
 * `rememberSwipeableTransactionListState(onDelete)`.
 *
 * Closes any open card when the backing list no longer contains its id. The edit action is a
 * navigation concern and stays a screen lambda — not owned by this controller.
 */
@Stable
class SwipeableTransactionListState(private val onDelete: (Transaction) -> Unit) {
    var currentlySwipedId: UUID? by mutableStateOf(null)
        private set

    fun open(id: UUID) {
        currentlySwipedId = id
    }

    fun delete(transaction: Transaction) {
        currentlySwipedId = null
        onDelete(transaction)
    }

    fun onListChanged(items: List<Transaction>) {
        val openId = currentlySwipedId ?: return
        if (items.none { it.id == openId }) {
            currentlySwipedId = null
        }
    }
}

@Composable
fun rememberSwipeableTransactionListState(onDelete: (Transaction) -> Unit): SwipeableTransactionListState =
    remember(onDelete) { SwipeableTransactionListState(onDelete) }
