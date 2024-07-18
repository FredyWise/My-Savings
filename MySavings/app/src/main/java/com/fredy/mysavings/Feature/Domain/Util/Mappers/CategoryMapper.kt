package com.fredy.mysavings.Feature.Domain.Util.Mappers

import com.fredy.mysavings.Feature.Domain.Model.Category
import com.fredy.mysavings.Feature.Presentation.ViewModels.CategoryViewModel.CategoryMap

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