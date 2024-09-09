package com.fredy.domain.util.mappers

import com.fredy.domain.model.Category


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