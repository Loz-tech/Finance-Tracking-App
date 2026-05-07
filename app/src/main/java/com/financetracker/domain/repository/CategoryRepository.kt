package com.financetracker.domain.repository

import com.financetracker.domain.model.Category
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface CategoryRepository {
    fun getAllCategories(): Flow<List<Category>>
    suspend fun getCategoryById(id: UUID): Category?
    suspend fun saveCategory(category: Category)
    suspend fun deleteCategory(category: Category)
    suspend fun getCategoryCount(): Int
    suspend fun seedDefaultCategories()
}
