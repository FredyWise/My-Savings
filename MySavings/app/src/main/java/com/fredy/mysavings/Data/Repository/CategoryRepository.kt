package com.fredy.mysavings.Data.Repository

import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Data.Database.Model.Category
import com.fredy.mysavings.Data.Enum.RecordType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface CategoryRepository {
    suspend fun upsertCategory(category: Category)
    suspend fun deleteCategory(category: Category)
    fun getCategory(categoryId: String): Flow<Category>
    fun getUserCategoriesOrderedByName(): Flow<List<Category>>
    fun getCategoriesUsingTypeOrderedByName(type: RecordType): Flow<List<Category>>
}

class CategoryRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
): CategoryRepository {
    private val categoryCollection = firestore.collection(
        "category"
    )

    override suspend fun upsertCategory(category: Category) {
        val currentUser = firebaseAuth.currentUser
        if (category.categoryId.isEmpty()) {
            val newCategoryRef = categoryCollection.document()
            newCategoryRef.set(
                category.copy(
                    categoryId = newCategoryRef.id,
                    userIdFk = currentUser!!.uid
                )
            )
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
        val currentUser = firebaseAuth.currentUser
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
        val currentUser = firebaseAuth.currentUser
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