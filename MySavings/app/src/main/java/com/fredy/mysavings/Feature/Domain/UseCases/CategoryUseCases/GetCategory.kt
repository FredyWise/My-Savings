package com.fredy.mysavings.Feature.Domain.UseCases.CategoryUseCases

import com.fredy.mysavings.Feature.Domain.Model.Category
import com.fredy.mysavings.Feature.Domain.Repository.CategoryRepository
import kotlinx.coroutines.flow.Flow

class GetCategory(
    private val repository: CategoryRepository
) {
    operator fun invoke(categoryId: String): Flow<Category> {
        return repository.getCategory(categoryId)
    }
}