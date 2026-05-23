package com.financetracker.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.financetracker.ui.theme.FinanceTrackingAppTheme

@Composable
fun ResetDataDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reset All Data?") },
        text = {
            Text(
                "This will permanently delete all transactions, custom categories, and budgets. This cannot be undone."
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Reset", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Preview
@Composable
private fun ResetDataDialogPreview() {
    FinanceTrackingAppTheme {
        ResetDataDialog(
            onDismiss = {},
            onConfirm = {}
        )
    }
}
