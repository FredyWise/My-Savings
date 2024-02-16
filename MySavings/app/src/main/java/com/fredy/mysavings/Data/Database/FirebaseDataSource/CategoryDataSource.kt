package com.fredy.mysavings.Data.Database.FirebaseDataSource

import android.util.Log
import com.fredy.mysavings.Data.Database.Model.Category
import com.fredy.mysavings.Util.TAG
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface CategoryDataSource {
    suspend fun upsertCategoryItem(category: Category)
    suspend fun deleteCategoryItem(category: Category)
    suspend fun getCategory(categoryId: String): Category
    suspend fun getUserCategoriesOrderedByName(userId: String): List<Category>
}


class CategoryDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : CategoryDataSource {
    private val categoryCollection = firestore.collection(
        "category"
    )

    override suspend fun upsertCategoryItem(//make sure the category already have uid // make sure to create the category id outside instead
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
                    TAG,
                    "Failed to get category: ${e.message}"
                )
                throw e
            }
        }
    }

    override suspend fun getUserCategoriesOrderedByName(userId: String): List<Category> {
        return withContext(Dispatchers.IO) {
            try {
                val querySnapshot = categoryCollection.whereEqualTo(
                    "userIdFk",
                    userId
                ).orderBy("categoryName").get().await()

                querySnapshot.toObjects()
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Failed to get user categorys: $e"
                )
                throw e
            }
        }
    }


}