package com.fredy.mysavings.Feature.Domain.Repository

import com.fredy.mysavings.Feature.Data.Database.Model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class FakeCategoryRepository : CategoryRepository {

    private val categories = mutableListOf<Category>()

    override suspend fun upsertCategory(category: Category): String {
        val existingCategory = categories.find { it.categoryId == category.categoryId }
        return if (existingCategory != null) {
            categories.remove(existingCategory)
            categories.add(category)
            category.categoryId
        } else {
            category.categoryId.also { categories.add(category) }
        }
    }

    override suspend fun deleteCategory(category: Category) {
        categories.remove(category)
    }

    override fun getCategory(categoryId: String): Flow<Category> {
        return flow { emit(categories.find { it.categoryId == categoryId }!!) }
    }

    override fun getUserCategories(userId: String): Flow<List<Category>> {
        return flow { emit(categories.filter { it.userIdFk == userId }) }
    }
}


