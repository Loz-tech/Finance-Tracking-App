package com.financetracker.ui.components.category

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.financetracker.domain.model.CategoryIcons
import com.financetracker.domain.model.IconStyle

@Composable
fun CategoryIcon(
    iconName: String,
    iconStyle: IconStyle,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    val imageVector = CategoryIcons.resolve(iconName, iconStyle)
    Icon(
        imageVector = imageVector,
        contentDescription = null,
        modifier = modifier,
        tint = tint
    )
}
