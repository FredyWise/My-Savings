package com.fredy.mysavings.Feature.Domain.UseCases.CategoryUseCases

import com.fredy.mysavings.Feature.Domain.Model.Category
import com.fredy.mysavings.Feature.Domain.Repository.CategoryRepository

class DeleteCategory(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(category: Category) {
        repository.deleteCategory(category)
    }
}