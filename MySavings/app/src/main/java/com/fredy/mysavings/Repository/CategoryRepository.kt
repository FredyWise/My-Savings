package com.fredy.mysavings.Repository

import com.fredy.mysavings.Data.RoomDatabase.Dao.CategoryDao
import com.fredy.mysavings.Data.RoomDatabase.Entity.Category
import com.fredy.mysavings.Data.RoomDatabase.Enum.RecordType
import com.fredy.mysavings.Data.RoomDatabase.SavingsDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface CategoryRepository {
    suspend fun upsertCategory(category: Category)
    suspend fun deleteCategory(category: Category)
    fun getCategory(categoryId: Int): Flow<Category>
    fun getUserCategoriesOrderedByName(): Flow<List<Category>>
    fun getCategoriesUsingTypeOrderedByName(type: RecordType): Flow<List<Category>>
}

class CategoryRepositoryImpl @Inject constructor(private val savingsDatabase: SavingsDatabase) : CategoryRepository {
    override suspend fun upsertCategory(category: Category) {
        savingsDatabase.categoryDao.upsertCategory(category)
    }

    override suspend fun deleteCategory(category: Category) {
        savingsDatabase.categoryDao.deleteCategory(category)
    }

    override fun getCategory(categoryId: Int): Flow<Category> {
        return savingsDatabase.categoryDao.getCategory(categoryId)
    }

    override fun getUserCategoriesOrderedByName(): Flow<List<Category>> {
        return savingsDatabase.categoryDao.getUserCategoriesOrderedByName()
    }

    override fun getCategoriesUsingTypeOrderedByName(type: RecordType): Flow<List<Category>> {
        return savingsDatabase.categoryDao.getCategoriesUsingTypeOrderedByName(type)
    }
}