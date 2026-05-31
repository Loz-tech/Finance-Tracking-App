package com.financetracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.financetracker.ui.categories.CategoryWithProgress
import java.math.BigDecimal

@Composable
fun CategoryCard(
    catWithProgress: CategoryWithProgress,
    iconStyle: com.financetracker.domain.model.IconStyle,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (catWithProgress.budgetLimit != null && catWithProgress.budgetLimit!! > BigDecimal.ZERO) {
        (catWithProgress.spent.toFloat() / catWithProgress.budgetLimit!!.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    val isOverBudget = catWithProgress.isOverBudget
    SectionCard(
        modifier = modifier,
        containerColor = if (isOverBudget) {
            MaterialTheme.colorScheme.errorContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerLow
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CategoryIcon(
                    iconName = catWithProgress.category.iconName,
                    iconStyle = iconStyle,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable(onClick = onIconClick)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    catWithProgress.category.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            Row {
                TextButton(onClick = onEdit) { Text("Edit") }
                TextButton(onClick = onDelete) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            }
        }
        if (catWithProgress.budgetLimit != null) {
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = if (isOverBudget) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                },
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
            )
        }
    }
}
