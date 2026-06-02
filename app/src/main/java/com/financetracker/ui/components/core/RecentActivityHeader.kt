package com.financetracker.ui.components.core

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.financetracker.ui.theme.FinanceTrackingAppTheme

@Composable
fun RecentActivityHeader(
    title: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    isEmpty: Boolean = false,
    modifier: Modifier = Modifier
) {
    val headerShape = if (isEmpty) {
        RoundedCornerShape(12.dp)
    } else {
        RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(headerShape)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall
        )
        if (actionLabel != null) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = if (onActionClick != null) Modifier.clickable(onClick = onActionClick) else Modifier
            )
        }
    }
}

@Preview
@Composable
private fun RecentActivityHeaderPreview() {
    FinanceTrackingAppTheme {
        RecentActivityHeader(
            title = "Recent Activity",
            actionLabel = "History",
            isEmpty = false
        )
    }
}

@Preview
@Composable
private fun RecentActivityHeaderEmptyPreview() {
    FinanceTrackingAppTheme {
        RecentActivityHeader(
            title = "Recent Activity",
            isEmpty = true
        )
    }
}
