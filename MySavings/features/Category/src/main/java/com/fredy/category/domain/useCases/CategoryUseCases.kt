package com.fredy.category.domain.useCases

data class CategoryUseCases(
    val upsertCategory: UpsertCategory,
    val deleteCategory: DeleteCategory,
    val getCategory: GetCategory,
    val getCategoryMapOrderedByName: GetCategoryMapOrderedByName,
    val getUserCategoryRecordsOrderedByDateTime: GetUserCategoryRecordsOrderedByDateTime,
    val updateRecordItemWithDeletedCategory: UpdateRecordItemWithDeletedCategory
)