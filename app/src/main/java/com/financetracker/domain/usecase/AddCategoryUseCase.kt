package com.financetracker.domain.usecase

import com.financetracker.domain.model.Category
import com.financetracker.domain.repository.CategoryRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class AddCategoryUseCase @Inject constructor(private val categoryRepository: CategoryRepository) {
    suspend operator fun invoke(name: String, iconName: String): Category {
        val maxOrder = categoryRepository.getAllCategories().first().maxOfOrNull { it.sortOrder } ?: -1
        val category = Category(name = name, iconName = iconName, sortOrder = maxOrder + 1)
        categoryRepository.saveCategory(category)
        return category
    }
}
