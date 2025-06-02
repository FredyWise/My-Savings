package com.fredy.category.domain.useCases

import com.fredy.domain.model.Category
import com.fredy.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow

class GetCategory(
    private val repository: CategoryRepository
) {
    operator fun invoke(categoryId: String): Flow<Category> {
        return repository.getCategory(categoryId)
    }
}