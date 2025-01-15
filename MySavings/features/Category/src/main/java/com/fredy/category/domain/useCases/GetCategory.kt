package com.fredy.category.domain.useCases

import com.fredy.category.domain.CategoryRepository
import com.fredy.domain.model.Category
import kotlinx.coroutines.flow.Flow

class GetCategory(
    private val repository: CategoryRepository
) {
    operator fun invoke(categoryId: String): Flow<Category> {
        return repository.getCategory(categoryId)
    }
}