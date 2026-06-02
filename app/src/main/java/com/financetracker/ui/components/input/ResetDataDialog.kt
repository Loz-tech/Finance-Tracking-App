package com.financetracker.ui.components.input

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.financetracker.R
import com.financetracker.ui.theme.FinanceTrackingAppTheme

@Composable
fun ResetDataDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_reset_title)) },
        text = {
            Text(
                stringResource(R.string.dialog_reset_body)
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.dialog_reset_confirm), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.dialog_cancel)) }
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
