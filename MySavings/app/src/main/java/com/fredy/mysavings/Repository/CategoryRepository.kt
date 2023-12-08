package com.fredy.mysavings.Repository

import com.fredy.mysavings.Data.Database.Entity.Category
import com.fredy.mysavings.Data.Enum.RecordType
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

interface CategoryRepository {
    suspend fun upsertCategory(category: Category)
    suspend fun deleteCategory(category: Category)
    fun getCategory(categoryId: String): Flow<Category>
    fun getUserCategoriesOrderedByName(): Flow<List<Category>>
    fun getCategoriesUsingTypeOrderedByName(type: RecordType): Flow<List<Category>>
}

class CategoryRepositoryImpl(): CategoryRepository {
    override suspend fun upsertCategory(category: Category) {
        val currentUser = Firebase.auth.currentUser
        val categoryCollection = Firebase.firestore.collection(
            "category"
        )
        if (category.categoryId.isEmpty()) {
            categoryCollection.add(
                category
            ).addOnSuccessListener { document ->
                categoryCollection.document(
                    document.id
                ).set(
                    category.copy(
                        categoryId = document.id,
                        userIdFk = currentUser!!.uid
                    )
                )
            }
        } else {
            categoryCollection.document(
                category.categoryId
            ).set(
                category.copy(
                    userIdFk = currentUser!!.uid
                )
            )
        }
    }

    override suspend fun deleteCategory(category: Category) {
        Firebase.firestore.collection("category").document(
            category.categoryId
        ).delete()
    }

    override fun getCategory(categoryId: String): Flow<Category> {
        return flow {
            val result = Firebase.firestore.collection(
                "category"
            ).document(
                categoryId
            ).get().await().toObject<Category>()!!
            emit(result)
        }
    }

    override fun getUserCategoriesOrderedByName() = callbackFlow<List<Category>> {
        val currentUser = Firebase.auth.currentUser
        val listener = Firebase.firestore.collection(
            "category"
        ).whereEqualTo(
            "userIdFk", currentUser!!.uid
        ).addSnapshotListener { value, error ->
            error?.let {
                close(it)
            }
            value?.let {
                val data = it.documents.map { document ->
                    document.toObject<Category>()!!
                }
                trySend(data)
            }
        }

        awaitClose {
            listener.remove()
        }
    }

    override fun getCategoriesUsingTypeOrderedByName(
        type: RecordType
    ) = callbackFlow<List<Category>> {
        val currentUser = Firebase.auth.currentUser
        val listener = Firebase.firestore.collection(
            "category"
        ).whereEqualTo(
            "userIdFk", currentUser!!.uid
        ).whereEqualTo(
            "categoryType", type.name
        ).addSnapshotListener { value, error ->
            error?.let {
                close(it)
            }
            value?.let {
                val data = it.documents.map { document ->
                    document.toObject<Category>()!!
                }
                trySend(data)
            }
        }

        awaitClose {
            listener.remove()
        }
    }
}