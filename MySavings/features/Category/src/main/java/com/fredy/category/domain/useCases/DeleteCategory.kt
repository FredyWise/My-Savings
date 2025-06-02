package com.fredy.category.domain.useCases


import com.fredy.domain.model.Category
import com.fredy.domain.repository.CategoryRepository


class DeleteCategory(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(category: Category) {
        repository.deleteCategory(category)
    }
}