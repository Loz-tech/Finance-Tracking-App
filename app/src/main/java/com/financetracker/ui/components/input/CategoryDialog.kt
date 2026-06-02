package com.financetracker.ui.components.input

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.financetracker.R
import com.financetracker.domain.model.Category

@Composable
fun CategoryDialog(category: Category?, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var name by remember(category?.id) { mutableStateOf(category?.name ?: "") }
    val title = if (category ==
        null
    ) {
        stringResource(R.string.dialog_add_category)
    } else {
        stringResource(R.string.dialog_edit_category)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.dialog_category_name)) },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name)
                        onDismiss()
                    }
                }
            ) { Text(stringResource(R.string.dialog_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.dialog_cancel)) }
        }
    )
}
