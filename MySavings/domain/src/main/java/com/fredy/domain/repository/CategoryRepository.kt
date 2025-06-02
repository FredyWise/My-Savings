package com.fredy.domain.repository

import com.fredy.domain.enums.SortType
import com.fredy.domain.model.Category
import com.fredy.domain.model.TrueRecord

import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun upsertCategory(category: Category): String
    suspend fun deleteCategory(category: Category)
    fun getCategory(categoryId: String): Flow<Category>
    fun getUserCategories(userId:String): Flow<List<Category>>
    fun getUserCategoryRecordsOrderedByDateTime(
        userId: String,
        categoryId: String,
        sortType: SortType
    ): Flow<List<TrueRecord>>
}

