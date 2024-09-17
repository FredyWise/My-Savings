package com.fredy.domain.mappers.categoryUIMapper

import com.fredy.domain.model.Category
import com.fredy.domain.model.CategoryMap


fun List<Category>.toCategoryMaps(): List<CategoryMap> {
    return this.groupBy {
        it.categoryType
    }.toSortedMap().map {
        CategoryMap(
            categoryType = it.key,
            categories = it.value
        )
    }
}