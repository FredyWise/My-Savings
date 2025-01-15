package com.fredy.category.viewModel

import com.fredy.domain.util.resource.DataError
import com.fredy.domain.util.resource.Resource
import com.fredy.domain.enums.RecordType
import com.fredy.domain.enums.SortType
import com.fredy.domain.model.Category
import com.fredy.domain.model.CategoryMap
import com.fredy.domain.model.RecordMap

data class CategoryState(
    val categoryMapsResource: Resource<List<CategoryMap>, DataError.Local> = Resource.Loading(),
    val categoryResource: Resource<List<Category>, DataError.Local> = Resource.Loading(),
    val recordMapsResource: Resource<List<RecordMap>, DataError.Local> = Resource.Loading(),
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