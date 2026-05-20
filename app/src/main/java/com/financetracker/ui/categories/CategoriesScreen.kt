package com.financetracker.ui.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.financetracker.domain.model.Category
import com.financetracker.ui.components.CategoryCard
import com.financetracker.ui.components.CategoryDialog

@Composable
fun CategoriesScreen(modifier: Modifier = Modifier, viewModel: CategoriesViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<Category?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
        CategoryDialog(
            category = null,
            onDismiss = { showAddDialog = false },
            onSave = { name, emoji ->
                viewModel.addCategory(name, emoji)
                showAddDialog = false
            }
        )
    }

    if (editingCategory != null) {
        CategoryDialog(
            category = editingCategory,
            onDismiss = { editingCategory = null },
            onSave = { name, emoji ->
                editingCategory?.let {
                    viewModel.updateCategory(it.copy(name = name, emoji = emoji))
                }
                editingCategory = null
            }
        )
    }
}
