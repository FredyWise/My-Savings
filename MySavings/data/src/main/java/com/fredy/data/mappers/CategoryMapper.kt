package com.fredy.data.mappers

import com.fredy.domain.model.CategoryMap
import com.fredy.data.database.dto.Category as DataCategory
import com.fredy.domain.model.Category as DomainCategory


fun DomainCategory.toDataCategory(): DataCategory {
    return DataCategory(
        categoryId,
        userIdFk,
        categoryName,
        categoryType,
        categoryIcon,
        categoryIconDescription,
    )
}

fun DataCategory.toDomainCategory(): DomainCategory {
    return DomainCategory(
        categoryId,
        userIdFk,
        categoryName,
        categoryType,
        categoryIcon,
        categoryIconDescription,
    )
}

fun List<DomainCategory>.toCategoryMaps(): List<CategoryMap> {
    return this.groupBy {
        it.categoryType
    }.toSortedMap().map {
        CategoryMap(
            categoryType = it.key,
            categories = it.value
        )
    }
}