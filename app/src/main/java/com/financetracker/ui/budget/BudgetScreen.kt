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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.financetracker.R
import com.financetracker.ui.components.budget.TotalBudgetEditor
import com.financetracker.ui.components.category.CategoryBudgetSliderCard
import com.financetracker.ui.components.core.SectionCard
import com.financetracker.ui.components.util.rememberIconStyle

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
                stringResource(R.string.budget_monthly_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            SectionCard(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.budget_total_limit)
            ) {
                TotalBudgetEditor(
                    value = totalInput,
                    onValueChange = { totalInput = it },
                    onSave = {
                        totalInput.toBigDecimalOrNull()?.let {
                            viewModel.setTotalBudget(it)
                        }
                    },
                    label = stringResource(R.string.budget_amount_label),
                    buttonLabel = stringResource(R.string.budget_save_total)
                )
            }

            Text(stringResource(R.string.budget_category_budgets), style = MaterialTheme.typography.titleSmall)
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
                contentDescription = stringResource(R.string.budget_recalculate)
            )
        }
    }
}
