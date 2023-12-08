package com.fredy.mysavings.ViewModels.Event

import com.fredy.mysavings.Data.Database.Entity.Category
import com.fredy.mysavings.Data.Enum.RecordType
import com.fredy.mysavings.Data.Enum.SortType

sealed interface CategoryEvent {
    object SaveCategory: CategoryEvent
    data class CategoryName(val categoryName: String): CategoryEvent
    data class CategoryTypes(val categoryType: RecordType): CategoryEvent
    data class CategoryIcon(
        val icon: Int,
        val iconDescription: String
    ): CategoryEvent
    data class ShowDialog(val category: Category): CategoryEvent
    object HideDialog: CategoryEvent
    data class SortCategory(val sortType: SortType): CategoryEvent
    data class DeleteCategory(val category: Category): CategoryEvent
    data class SearchCategory(val name: String): CategoryEvent

}
