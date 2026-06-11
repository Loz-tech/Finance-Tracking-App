package com.financetracker.ui.components.core

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.financetracker.R
import com.financetracker.domain.model.IconStyle
import com.financetracker.domain.model.Transaction
import java.util.UUID
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

private enum class SwipeAnchor { Closed, Open }

/**
 * A transaction card that can be swiped left to reveal full-width edit/delete actions.
 * Background actions are 50 % width each. Only one card can be open at a time.
 */
@Composable
fun SwipeableTransactionCard(
    transaction: Transaction,
    iconStyle: IconStyle,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    currentlySwipedId: UUID? = null,
    onSwipeOpened: ((UUID) -> Unit)? = null,
    useCard: Boolean = true,
    iconSize: Dp = 44.dp,
    showDate: Boolean = false,
    cardCornerRadius: Dp = 12.dp,
    horizontalPadding: Dp = 12.dp,
    verticalPadding: Dp = 12.dp,
    shape: CornerBasedShape = RoundedCornerShape(0.dp)
) {
    val scope = rememberCoroutineScope()
    val editLabel = stringResource(R.string.category_edit)
    val deleteLabel = stringResource(R.string.category_delete)

    BoxWithConstraints(
        modifier = modifier
            .clip(shape)
            .fillMaxWidth()
    ) {
        val density = LocalDensity.current
        val cardWidthPx = with(density) { maxWidth.toPx() }

        val state = remember {
            AnchoredDraggableState(initialValue = SwipeAnchor.Closed)
        }

        val anchors = DraggableAnchors {
            SwipeAnchor.Closed at 0f
            SwipeAnchor.Open at -cardWidthPx
        }
        SideEffect {
            state.updateAnchors(anchors)
        }

        // Mutual exclusivity: close this card when another opens.
        LaunchedEffect(currentlySwipedId) {
            if (currentlySwipedId != transaction.id && state.currentValue == SwipeAnchor.Open) {
                state.animateTo(SwipeAnchor.Closed)
            }
        }

        // Notify parent when this card settles to Open.
        LaunchedEffect(state.currentValue) {
            if (state.currentValue == SwipeAnchor.Open && onSwipeOpened != null) {
                onSwipeOpened(transaction.id)
            }
        }

        val flingBehavior = AnchoredDraggableDefaults.flingBehavior(
            state = state,
            positionalThreshold = { distance -> distance * 0.5f },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )

        // Background actions — each 50 % width.
        Row(
            modifier = Modifier.matchParentSize(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Edit (left half)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable(
                        enabled = state.currentValue == SwipeAnchor.Open
                    ) {
                        scope.launch {
                            state.snapTo(SwipeAnchor.Closed)
                            onEdit()
                        }
                    }
                    .clearAndSetSemantics { },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.category_edit),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            // Delete (right half)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .clickable(
                        enabled = state.currentValue == SwipeAnchor.Open
                    ) {
                        scope.launch {
                            state.snapTo(SwipeAnchor.Closed)
                            onDelete()
                        }
                    }
                    .clearAndSetSemantics { },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.category_delete),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }

        // Foreground card — shifts via offset.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .anchoredDraggable(
                    state = state,
                    orientation = Orientation.Horizontal,
                    flingBehavior = flingBehavior
                )
                .offset {
                    IntOffset(state.requireOffset().roundToInt(), 0)
                }
                .semantics(mergeDescendants = true) {
                    customActions = listOf(
                        CustomAccessibilityAction(
                            label = editLabel,
                            action = {
                                onEdit()
                                true
                            }
                        ),
                        CustomAccessibilityAction(
                            label = deleteLabel,
                            action = {
                                onDelete()
                                true
                            }
                        )
                    )
                }
        ) {
            val cardBackground = if (useCard) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceContainerLow
            }
            TransactionCard(
                transaction = transaction,
                iconStyle = iconStyle,
                modifier = Modifier.background(cardBackground),
                useCard = useCard,
                iconSize = iconSize,
                showDate = showDate,
                cardCornerRadius = cardCornerRadius,
                horizontalPadding = horizontalPadding,
                verticalPadding = verticalPadding
            )
        }
    }
}
