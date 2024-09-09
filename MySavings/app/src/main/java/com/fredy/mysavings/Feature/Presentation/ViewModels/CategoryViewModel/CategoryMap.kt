package com.fredy.mysavings.Feature.Presentation.ViewModels.CategoryViewModel

import com.fredy.data.enums.RecordType
import com.fredy.domain.model.Category

data class CategoryMap(
    val categoryType: RecordType = RecordType.Expense,
    val categories: List<Category> = emptyList()
)