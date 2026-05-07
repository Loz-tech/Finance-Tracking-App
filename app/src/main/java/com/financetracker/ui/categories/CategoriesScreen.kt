package com.financetracker.ui.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.math.BigDecimal

@Composable
fun CategoriesScreen(
    modifier: Modifier = Modifier,
    viewModel: CategoriesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<com.financetracker.domain.model.Category?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(uiState.categoriesWithProgress, key = { it.category.id }) { item ->
                CategoryCard(
                    catWithProgress = item,
                    onEdit = { editingCategory = item.category },
                    onDelete = { viewModel.deleteCategory(item.category) }
                )
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    if (showAddDialog) {
        AddCategoryDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, emoji ->
                viewModel.addCategory(name, emoji)
                showAddDialog = false
            }
        )
    }

    if (editingCategory != null) {
        EditCategoryDialog(
            category = editingCategory!!,
            onDismiss = { editingCategory = null },
            onSave = { viewModel.updateCategory(it) }
        )
    }
}

@Composable
private fun CategoryCard(
    catWithProgress: CategoryWithProgress,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val progress = if (catWithProgress.budgetLimit != null && catWithProgress.budgetLimit!! > BigDecimal.ZERO) {
        (catWithProgress.spent.toFloat() / catWithProgress.budgetLimit!!.toFloat()).coerceIn(0f, 1f)
    } else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (catWithProgress.isOverBudget) MaterialTheme.colorScheme.errorContainer
            else MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(catWithProgress.category.emoji, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(catWithProgress.category.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                }
                Row {
                    TextButton(onClick = onEdit) { Text("Edit") }
                    TextButton(onClick = onDelete) { Text("Delete", color = MaterialTheme.colorScheme.error) }
                }
            }
            if (catWithProgress.budgetLimit != null) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = if (catWithProgress.isOverBudget) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )
            }
        }
    }
}

@Composable
private fun AddCategoryDialog(onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf("📦") }
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Category") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = emoji, onValueChange = { emoji = it }, label = { Text("Emoji") }, singleLine = true)
            }
        },
        confirmButton = { TextButton(onClick = { if (name.isNotBlank()) onSave(name, emoji) }) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun EditCategoryDialog(category: com.financetracker.domain.model.Category, onDismiss: () -> Unit, onSave: (com.financetracker.domain.model.Category) -> Unit) {
    var name by remember { mutableStateOf(category.name) }
    var emoji by remember { mutableStateOf(category.emoji) }
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Category") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = emoji, onValueChange = { emoji = it }, label = { Text("Emoji") }, singleLine = true)
            }
        },
        confirmButton = { TextButton(onClick = { onSave(category.copy(name = name, emoji = emoji)); onDismiss() }) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
