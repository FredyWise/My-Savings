package com.fredy.mysavings.Data.Repository

import co.yml.charts.common.extensions.isNotNull
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

class CategoryRepositoryImpl: CategoryRepository {
    private val categoryCollection = Firebase.firestore.collection(
        "category"
    )

    override suspend fun upsertCategory(category: Category) {
        val currentUser = Firebase.auth.currentUser
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
        categoryCollection.document(
            category.categoryId
        ).delete()
    }

    override fun getCategory(categoryId: String): Flow<Category> {
        return flow {
            val result = categoryCollection.document(
                categoryId
            ).get().await().toObject<Category>() ?: Category()
            emit(result)
        }
    }

    override fun getUserCategoriesOrderedByName() = callbackFlow<List<Category>> {
        val currentUser = Firebase.auth.currentUser
        val listener = categoryCollection.whereEqualTo(
            "userIdFk",
            if (currentUser.isNotNull()) currentUser!!.uid else ""
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
        val listener = categoryCollection.whereEqualTo(
            "userIdFk",
            if (currentUser.isNotNull()) currentUser!!.uid else ""
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