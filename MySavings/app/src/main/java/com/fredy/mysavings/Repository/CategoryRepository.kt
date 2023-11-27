package com.fredy.mysavings.Repository

import com.fredy.mysavings.Data.GoogleAuth.GoogleAuthUiClient
import com.fredy.mysavings.Data.Database.Entity.Category
import com.fredy.mysavings.Data.Database.Enum.RecordType
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
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
    private val firebaseAuth: FirebaseAuth,
    private val googleAuthUiClient: GoogleAuthUiClient,
): CategoryRepository {
    val currentUser = googleAuthUiClient.getSignedInUser(
        firebaseAuth
    )
    override suspend fun upsertCategory(category: Category) {
        val categoryCollection = Firebase.firestore.collection("category")
        if (category.categoryId.isEmpty()) {
            categoryCollection.add(
                category
            ).addOnSuccessListener { document ->
                categoryCollection.document(
                    document.id
                ).set(
                    category.copy(
                        categoryId = document.id,
                        userIdFk = currentUser!!.firebaseUserId
                    )
                )
            }
        }else{
            categoryCollection.document(
                category.categoryId
            ).set(
                category.copy(
                    userIdFk = currentUser!!.firebaseUserId
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
        val listener = Firebase.firestore.collection(
            "category"
        ).whereEqualTo(
            "userIdFk",
            currentUser!!.firebaseUserId
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
        val listener = Firebase.firestore.collection(
            "category"
        ).whereEqualTo(
            "userIdFk",
            currentUser!!.firebaseUserId
        ).whereEqualTo(
            "categoryType",
            type.name
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