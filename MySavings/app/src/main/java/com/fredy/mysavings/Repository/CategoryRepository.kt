package com.fredy.mysavings.Repository

import com.fredy.mysavings.Data.RoomDatabase.Dao.CategoryDao
import com.fredy.mysavings.Data.RoomDatabase.Entity.Category
import com.fredy.mysavings.Data.RoomDatabase.Enum.RecordType
import com.fredy.mysavings.Data.RoomDatabase.SavingsDatabase
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface CategoryRepository {
    suspend fun upsertCategory(category: Category)
    suspend fun deleteCategory(category: Category)
    fun getCategory(categoryId: String): Flow<Category>
    fun getUserCategoriesOrderedByName(): Flow<List<Category>>
    fun getCategoriesUsingTypeOrderedByName(type: RecordType): Flow<List<Category>>
}

class CategoryRepositoryImpl @Inject constructor(private val savingsDatabase: SavingsDatabase) : CategoryRepository {
    override suspend fun upsertCategory(category: Category) {
        Firebase.firestore
            .collection("record")
            .add(category)
            .addOnSuccessListener { documentReference ->
                val generatedId = documentReference.id
                category.categoryId = generatedId
            }
        savingsDatabase.categoryDao.upsertCategory(category)
    }

    override suspend fun deleteCategory(category: Category) {
        Firebase.firestore
            .collection("record")
            .document(category.categoryId)
            .delete()
        savingsDatabase.categoryDao.deleteCategory(category)
    }

    override fun getCategory(categoryId: String): Flow<Category> {
        return savingsDatabase.categoryDao.getCategory(categoryId)
    }

    override fun getUserCategoriesOrderedByName(): Flow<List<Category>> {
        return savingsDatabase.categoryDao.getUserCategoriesOrderedByName()
    }

    override fun getCategoriesUsingTypeOrderedByName(type: RecordType): Flow<List<Category>> {
        return savingsDatabase.categoryDao.getCategoriesUsingTypeOrderedByName(type)
    }
}