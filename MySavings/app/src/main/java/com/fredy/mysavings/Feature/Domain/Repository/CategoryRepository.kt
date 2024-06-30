package com.fredy.mysavings.Feature.Domain.Repository

import com.fredy.mysavings.Feature.Domain.Model.Category

import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun upsertCategory(category: Category): String
    suspend fun deleteCategory(category: Category)
    fun getCategory(categoryId: String): Flow<Category>
    fun getUserCategories(userId:String): Flow<List<Category>>
}

