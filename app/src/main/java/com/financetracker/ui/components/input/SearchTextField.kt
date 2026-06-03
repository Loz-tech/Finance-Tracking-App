package com.financetracker.ui.components.input

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.financetracker.ui.theme.FinanceTrackingAppTheme

@Composable
fun SearchTextField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
    placeholder: String
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = if (focusRequester != null) {
            modifier.focusRequester(focusRequester)
        } else {
            modifier
        },
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp)
    )
}

@Preview
@Composable
private fun SearchTextFieldEmptyPreview() {
    FinanceTrackingAppTheme {
        SearchTextField(
            query = "",
            onQueryChange = {},
            onClear = {},
            placeholder = "Search expenses..."
        )
    }
}

@Preview
@Composable
private fun SearchTextFieldFilledPreview() {
    FinanceTrackingAppTheme {
        SearchTextField(
            query = "coffee",
            onQueryChange = {},
            onClear = {},
            placeholder = "Search expenses..."
        )
    }
}
