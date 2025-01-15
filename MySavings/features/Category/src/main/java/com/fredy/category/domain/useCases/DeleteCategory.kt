package com.fredy.category.domain.useCases

import com.fredy.category.domain.CategoryRepository
import com.fredy.domain.model.Category


class DeleteCategory(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(category: Category) {
        repository.deleteCategory(category)
    }
}