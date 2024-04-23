package com.fredy.mysavings.Feature.Domain.UseCases.CategoryUseCases

data class CategoryUseCases(
    val upsertCategory: UpsertCategory,
    val deleteCategory: DeleteCategory,
    val getCategory: GetCategory,
    val getCategoryMapOrderedByName: GetCategoryMapOrderedByName
)