package com.financetracker.ui.components.budget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.financetracker.ui.components.input.AmountInput
import com.financetracker.ui.theme.FinanceTrackingAppTheme

@Composable
fun TotalBudgetEditor(
    value: String,
    onValueChange: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    buttonLabel: String,
    isError: Boolean = false
) {
    Column(modifier = modifier) {
        AmountInput(
            value = value,
            onValueChange = onValueChange,
            label = label,
            isError = isError,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(buttonLabel)
        }
    }
}

@Preview
@Composable
private fun TotalBudgetEditorPreview() {
    FinanceTrackingAppTheme {
        TotalBudgetEditor(
            value = "1000.00",
            onValueChange = {},
            onSave = {},
            label = "Amount",
            buttonLabel = "Save Total Budget"
        )
    }
}

@Preview
@Composable
private fun TotalBudgetEditorErrorPreview() {
    FinanceTrackingAppTheme {
        TotalBudgetEditor(
            value = "abc",
            onValueChange = {},
            onSave = {},
            label = "Amount",
            buttonLabel = "Save Total Budget",
            isError = true
        )
    }
}
