package com.financetracker.ui.budget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BudgetScreen(modifier: Modifier = Modifier, viewModel: BudgetViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
    var totalInput by remember { mutableStateOf("") }

    LaunchedEffect(uiState.totalBudget) {
        totalInput = uiState.totalBudget.toPlainString()
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Monthly Budget", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)

            // Total budget
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Monthly Limit", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = totalInput,
                        onValueChange = { totalInput = it },
                        label = { Text("Amount") },
                        prefix = { Text("$  ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    androidx.compose.material3.Button(
                        onClick = { totalInput.toBigDecimalOrNull()?.let { viewModel.setTotalBudget(it) } },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Save Total Budget") }
                }
            }

            // Per-category budgets
            Text("Category Budgets", style = MaterialTheme.typography.titleSmall)
            uiState.categorySliders.forEach { slider ->
                var draftLimit by remember(slider.category.id) { mutableStateOf(slider.limit.toFloat()) }
                var draftText by remember(slider.category.id) { mutableStateOf(slider.limit.toPlainString()) }

                var expanded by remember(slider.category.id) { mutableStateOf(false) }

                LaunchedEffect(slider.limit) {
                    draftLimit = slider.limit.toFloat()
                    draftText = slider.limit.toPlainString()
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "${slider.category.emoji} ${slider.category.name}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                currencyFormatter.format(slider.limit),
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
                                    listOf(50, 100, 200, 500).forEach { preset ->
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
                                            viewModel.setCategoryBudget(slider.category, amount)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                ) { Text("Save") }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(64.dp))
        }

        FloatingActionButton(
            onClick = { viewModel.recalculateTotalBudget() },
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 16.dp),
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Recalculate"
            )
        }
    }
}
