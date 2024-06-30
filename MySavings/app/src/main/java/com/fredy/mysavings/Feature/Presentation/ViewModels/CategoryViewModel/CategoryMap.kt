package com.fredy.mysavings.Feature.Presentation.ViewModels.CategoryViewModel

import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Domain.Model.Category

data class CategoryMap(
    val categoryType: RecordType = RecordType.Expense,
    val categories: List<Category> = emptyList()
)