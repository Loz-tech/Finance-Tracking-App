package com.financetracker.ui.components.category

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.financetracker.domain.model.Category
import com.financetracker.domain.model.IconStyle
import com.financetracker.ui.components.core.SectionCard
import com.financetracker.ui.preview.PreviewData
import com.financetracker.ui.theme.FinanceTrackingAppTheme
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CategoryBudgetSliderCard(
    category: Category,
    iconStyle: IconStyle,
    limit: BigDecimal,
    spent: BigDecimal,
    onSave: (BigDecimal) -> Unit,
    modifier: Modifier = Modifier,
    presets: List<Int> = listOf(50, 100, 200, 500)
) {
    var draftLimit by remember(category.id) { mutableStateOf(limit.toFloat()) }
    var draftText by remember(category.id) { mutableStateOf(limit.toPlainString()) }
    var expanded by remember(category.id) { mutableStateOf(false) }
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())

    LaunchedEffect(limit) {
        draftLimit = limit.toFloat()
        draftText = limit.toPlainString()
    }

    SectionCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CategoryIcon(
                    iconName = category.iconName,
                    iconStyle = iconStyle,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    category.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                currencyFormatter.format(limit),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value = draftLimit,
            onValueChange = {
                draftLimit = it
                draftText = BigDecimal(it.toDouble()).toPlainString()
            },
            valueRange = 0f..2000f,
            modifier = Modifier.fillMaxWidth()
        )
        AnimatedVisibility(visible = expanded) {
            Column {
                OutlinedTextField(
                    value = draftText,
                    onValueChange = { text ->
                        draftText = text
                        text.toFloatOrNull()?.let { draftLimit = it.coerceIn(0f, 2000f) }
                    },
                    label = { Text("Amount") },
                    prefix = { Text("$  ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presets.forEach { preset ->
                        SuggestionChip(
                            onClick = {
                                draftLimit = preset.toFloat()
                                draftText = preset.toString()
                            },
                            label = { Text("$$preset") }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.material3.Button(
                    onClick = {
                        draftText.toBigDecimalOrNull()?.let { amount ->
                            onSave(amount)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Save") }
            }
        }
    }
}

@Preview
@Composable
private fun CategoryBudgetSliderCardPreview() {
    FinanceTrackingAppTheme {
        CategoryBudgetSliderCard(
            category = PreviewData.foodCategory,
            iconStyle = IconStyle.FILLED,
            limit = BigDecimal("300.00"),
            spent = BigDecimal("127.50"),
            onSave = {}
        )
    }
}
