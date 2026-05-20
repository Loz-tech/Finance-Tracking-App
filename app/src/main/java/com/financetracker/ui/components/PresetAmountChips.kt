package com.financetracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.financetracker.ui.theme.FinanceTrackingAppTheme

@Composable
fun PresetAmountChips(presets: List<Int>, onPresetSelected: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        presets.forEach { preset ->
            SuggestionChip(
                onClick = { onPresetSelected(preset) },
                label = { Text("$$preset") }
            )
        }
    }
}

@Preview
@Composable
private fun PresetAmountChipsPreview() {
    FinanceTrackingAppTheme {
        PresetAmountChips(
            presets = listOf(50, 100, 200, 500),
            onPresetSelected = {}
        )
    }
}
