package com.financetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.financetracker.ui.theme.AccentColor
import com.financetracker.ui.theme.FinanceTrackingAppTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AccentColorPicker(selectedIndex: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AccentColor.entries.forEachIndexed { index, accent ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .then(
                            if (index == selectedIndex) {
                                Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                            } else {
                                Modifier.border(1.dp, Color.Transparent, CircleShape)
                            }
                        )
                        .clickable { onSelect(index) }
                        .background(accent.primaryColor, CircleShape)
                )
                Text(accent.label, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Preview
@Composable
private fun AccentColorPickerPreview() {
    FinanceTrackingAppTheme {
        AccentColorPicker(
            selectedIndex = 0,
            onSelect = {}
        )
    }
}
