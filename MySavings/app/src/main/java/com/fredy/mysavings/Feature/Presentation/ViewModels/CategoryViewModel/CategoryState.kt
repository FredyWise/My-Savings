package com.fredy.mysavings.Feature.Presentation.ViewModels.CategoryViewModel

import com.fredy.data.enums.RecordType
import com.fredy.data.enums.SortType
import com.fredy.domain.model.Category
import com.fredy.domain.model.RecordMap

data class CategoryState(
    val categoryResource: Resource<List<CategoryMap>> = Resource.Loading(),
    val recordMapsResource: Resource<List<RecordMap>> = Resource.Loading(),
    val category: Category = Category(),
    val categoryId: String = "",
    val categoryName: String = "",
    val categoryType: RecordType = RecordType.Expense,
    val categoryIcon: Int = 0,
    val categoryIconDescription: String = "",
    val isAddingCategory: Boolean = false,
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val sortType: SortType = SortType.ASCENDING
)