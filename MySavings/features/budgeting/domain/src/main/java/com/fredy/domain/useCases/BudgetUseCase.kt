package com.fredy.domain.useCases

data class CategoryUseCases(
    val upsertCategory: UpsertCategory,
    val deleteCategory: DeleteCategory,
    val getCategoryMapOrderedByName: GetCategoryMapOrderedByName,
    val getUserCategoryRecordsOrderedByDateTime: GetUserCategoryRecordsOrderedByDateTime,
    val updateRecordItemWithDeletedCategory: UpdateRecordItemWithDeletedCategory
)