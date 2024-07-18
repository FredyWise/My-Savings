package com.fredy.mysavings.Feature.Presentation.ViewModels.CategoryViewModel

import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Data.Enum.SortType
import com.fredy.mysavings.Feature.Domain.Model.Category
import com.fredy.mysavings.Feature.Domain.Model.RecordMap
import com.fredy.mysavings.Feature.Domain.Util.Resource

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