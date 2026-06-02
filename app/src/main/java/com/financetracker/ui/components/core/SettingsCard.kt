package com.financetracker.ui.components.core

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.financetracker.ui.theme.FinanceTrackingAppTheme

@Composable
fun SettingsCard(title: String, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    SectionCard(
        modifier = modifier,
        title = title,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        content = content
    )
}

@Preview
@Composable
private fun SettingsCardPreview() {
    FinanceTrackingAppTheme {
        SettingsCard(title = "Theme") {
            Text("Dark mode toggle here")
        }
    }
}
