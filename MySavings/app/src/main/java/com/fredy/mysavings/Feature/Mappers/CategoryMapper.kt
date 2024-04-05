package com.fredy.mysavings.Feature.Mappers

import com.fredy.mysavings.Feature.Data.Database.Model.Category
import com.fredy.mysavings.ViewModels.CategoryMap

fun List<Category>.toCategoryMaps():List<CategoryMap>{
    return this.groupBy {
        it.categoryType
    }.toSortedMap().map {
        CategoryMap(
            categoryType = it.key,
            categories = it.value
        )
    }
}