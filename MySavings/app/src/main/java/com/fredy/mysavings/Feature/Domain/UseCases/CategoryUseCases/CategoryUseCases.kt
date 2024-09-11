package com.fredy.mysavings.Feature.Domain.UseCases.CategoryUseCases

import com.fredy.domain.useCases.CategoryUseCases.DeleteCategory
import com.fredy.domain.useCases.CategoryUseCases.GetCategory
import com.fredy.domain.useCases.CategoryUseCases.GetCategoryMapOrderedByName
import com.fredy.domain.useCases.CategoryUseCases.UpsertCategory

data class CategoryUseCases(
    val upsertCategory: UpsertCategory,
    val deleteCategory: DeleteCategory,
    val getCategory: GetCategory,
    val getCategoryMapOrderedByName: GetCategoryMapOrderedByName
)