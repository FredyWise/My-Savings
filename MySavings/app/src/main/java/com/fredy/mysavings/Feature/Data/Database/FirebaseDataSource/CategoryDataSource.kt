package com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource

import com.fredy.mysavings.Util.Log
import com.fredy.mysavings.Feature.Domain.Model.Category

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface CategoryDataSource {
    suspend fun upsertCategoryItem(category: Category)
    suspend fun deleteCategoryItem(category: Category)
    suspend fun getCategory(categoryId: String): Category
    suspend fun getUserCategoriesOrderedByName(userId: String): Flow<List<Category>>
}


class CategoryDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : CategoryDataSource {
    private val categoryCollection = firestore.collection(
        "category"
    )

    override suspend fun upsertCategoryItem(
        category: Category
    ) {
        categoryCollection.document(
            category.categoryId
        ).set(
            category
        )
    }

    override suspend fun deleteCategoryItem(
        category: Category
    ) {
        categoryCollection.document(category.categoryId).delete()
    }

    override suspend fun getCategory(categoryId: String): Category {
        return withContext(Dispatchers.IO) {
            try {
                categoryCollection.document(categoryId).get().await().toObject<Category>()
                    ?: throw Exception(
                        "Category Not Found"
                    )
            } catch (e: Exception) {
                Log.e(
                    "Failed to get category: ${e.message}"
                )
                throw e
            }
        }
    }

    override suspend fun getUserCategoriesOrderedByName(userId: String): Flow<List<Category>> {
        return withContext(Dispatchers.IO) {
            try {
                val querySnapshot = categoryCollection.whereEqualTo(
                    "userIdFk",
                    userId
                ).orderBy("categoryName").snapshots()

                querySnapshot.map { it.toObjects()}
            } catch (e: Exception) {
                Log.e(
                    "Failed to get user categories: $e"
                )
                throw e
            }
        }
    }


}