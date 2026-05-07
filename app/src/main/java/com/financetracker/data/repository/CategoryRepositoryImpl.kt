package com.financetracker.data.repository

import com.financetracker.data.local.db.CategoryDao
import com.financetracker.data.local.entity.CategoryEntity
import com.financetracker.domain.model.Category
import com.financetracker.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> =
        categoryDao.getAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getCategoryById(id: UUID): Category? =
        categoryDao.getById(id)?.toDomain()

    override suspend fun saveCategory(category: Category) {
        categoryDao.insert(category.toEntity())
    }

    override suspend fun deleteCategory(category: Category) {
        categoryDao.delete(category.toEntity())
    }

    override suspend fun getCategoryCount(): Int = categoryDao.count()

    override suspend fun seedDefaultCategories() {
        if (categoryDao.count() > 0) return

        val defaults = listOf(
            CategoryEntity(name = "Food & Dining", emoji = "🍔", isDefault = true, sortOrder = 0),
            CategoryEntity(name = "Transport", emoji = "🚗", isDefault = true, sortOrder = 1),
            CategoryEntity(name = "Housing", emoji = "🏠", isDefault = true, sortOrder = 2),
            CategoryEntity(name = "Entertainment", emoji = "🎮", isDefault = true, sortOrder = 3),
            CategoryEntity(name = "Shopping", emoji = "🛒", isDefault = true, sortOrder = 4),
            CategoryEntity(name = "Health", emoji = "💊", isDefault = true, sortOrder = 5),
            CategoryEntity(name = "Education", emoji = "📚", isDefault = true, sortOrder = 6),
            CategoryEntity(name = "Other", emoji = "📦", isDefault = true, sortOrder = 7)
        )
        categoryDao.insertAll(defaults)
    }

    private fun CategoryEntity.toDomain() = Category(
        id = id,
        name = name,
        emoji = emoji,
        colorHex = colorHex,
        isDefault = isDefault,
        sortOrder = sortOrder
    )

    private fun Category.toEntity() = CategoryEntity(
        id = id,
        name = name,
        emoji = emoji,
        colorHex = colorHex,
        isDefault = isDefault,
        sortOrder = sortOrder
    )
}
