package com.financetracker.ui.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.financetracker.ui.components.AmountInput
import com.financetracker.ui.components.CategoryBudgetSliderCard
import com.financetracker.ui.components.rememberIconStyle

@Composable
fun BudgetScreen(modifier: Modifier = Modifier, viewModel: BudgetViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val iconStyle = rememberIconStyle()
    var totalInput by remember { mutableStateOf("") }

    LaunchedEffect(uiState.totalBudget) {
        totalInput = uiState.totalBudget.toPlainString()
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Monthly Budget",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Monthly Limit", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    AmountInput(
                        value = totalInput,
                        onValueChange = { totalInput = it },
                        label = "Amount",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    androidx.compose.material3.Button(
                        onClick = {
                            totalInput.toBigDecimalOrNull()?.let {
                                viewModel.setTotalBudget(it)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Save Total Budget") }
                }
            }

            Text("Category Budgets", style = MaterialTheme.typography.titleSmall)
            uiState.categorySliders.forEach { slider ->
                CategoryBudgetSliderCard(
                    category = slider.category,
                    iconStyle = iconStyle,
                    limit = slider.limit,
                    spent = slider.spent,
                    onSave = { amount ->
                        viewModel.setCategoryBudget(slider.category, amount)
                    }
                )
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
