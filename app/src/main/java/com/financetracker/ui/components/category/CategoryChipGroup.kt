package com.financetracker.ui.components.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.financetracker.domain.model.Category
import com.financetracker.domain.model.IconStyle
import com.financetracker.ui.preview.PreviewData
import com.financetracker.ui.theme.FinanceTrackingAppTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryChipGroup(
    categories: List<Category>,
    selectedCategory: Category?,
    onCategorySelected: (Category) -> Unit,
    iconStyle: IconStyle,
    modifier: Modifier = Modifier,
    label: String? = null
) {
    if (label != null) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            CategoryChip(
                category = category,
                iconStyle = iconStyle,
                selected = category.id == selectedCategory?.id,
                onClick = { onCategorySelected(category) }
            )
        }
    }
}

@Preview
@Composable
private fun CategoryChipGroupPreview() {
    FinanceTrackingAppTheme {
        CategoryChipGroup(
            categories = PreviewData.categories,
            selectedCategory = PreviewData.foodCategory,
            onCategorySelected = {},
            iconStyle = IconStyle.FILLED
        )
    }
}

@Preview
@Composable
private fun CategoryChipGroupNoneSelectedPreview() {
    FinanceTrackingAppTheme {
        CategoryChipGroup(
            categories = PreviewData.categories,
            selectedCategory = null,
            onCategorySelected = {},
            iconStyle = IconStyle.FILLED
        )
    }
}
