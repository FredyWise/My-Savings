package com.fredy.mysavings.ui.Repository

import com.fredy.mysavings.Data.RoomDatabase.Dao.CategoryDao
import com.fredy.mysavings.Data.RoomDatabase.Entity.Category
import com.fredy.mysavings.Data.RoomDatabase.Enum.CategoryType
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun upsertCategory(category: Category)
    suspend fun deleteCategory(category: Category)
    fun getCategory(categoryId: Int): Flow<Category>
    fun getUserCategoriesOrderedByName(): Flow<List<Category>>
    fun getCategoriesUsingTypeOrderedByName(type: CategoryType): Flow<List<Category>>
}

class CategoryRepositoryImpl(private val categoryDao: CategoryDao) : CategoryRepository {
    override suspend fun upsertCategory(category: Category) {
        categoryDao.upsertCategory(category)
    }

    override suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category)
    }

    override fun getCategory(categoryId: Int): Flow<Category> {
        return categoryDao.getCategory(categoryId)
    }

    override fun getUserCategoriesOrderedByName(): Flow<List<Category>> {
        return categoryDao.getUserCategoriesOrderedByName()
    }

    override fun getCategoriesUsingTypeOrderedByName(type: CategoryType): Flow<List<Category>> {
        return categoryDao.getCategoriesUsingTypeOrderedByName(type)
    }
}