package com.financetracker.ui.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.financetracker.ui.theme.FinanceTrackingAppTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePicker(onRangeSelected: (LocalDate, LocalDate) -> Unit, onDismiss: () -> Unit = {}) {
    var showStartDatePicker by remember { mutableStateOf(true) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var pendingCustomStart by remember { mutableStateOf<LocalDate?>(null) }

    // Start date picker
    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = {
                showStartDatePicker = false
                onDismiss()
            },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault()).toLocalDate()
                        pendingCustomStart = date
                        showStartDatePicker = false
                        showEndDatePicker = true
                    } ?: run {
                        showStartDatePicker = false
                        onDismiss()
                    }
                }) { Text("Next") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showStartDatePicker = false
                    onDismiss()
                }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // End date picker
    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = {
                showEndDatePicker = false
                pendingCustomStart = null
                onDismiss()
            },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val endDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault()).toLocalDate()
                        pendingCustomStart?.let { startDate ->
                            onRangeSelected(startDate, endDate)
                        }
                        showEndDatePicker = false
                        pendingCustomStart = null
                    } ?: run {
                        showEndDatePicker = false
                        pendingCustomStart = null
                        onDismiss()
                    }
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showEndDatePicker = false
                    pendingCustomStart = null
                    onDismiss()
                }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Preview
@Composable
private fun DateRangePickerPreview() {
    FinanceTrackingAppTheme {
        DateRangePicker(
            onRangeSelected = { _, _ -> },
            onDismiss = {}
        )
    }
}
