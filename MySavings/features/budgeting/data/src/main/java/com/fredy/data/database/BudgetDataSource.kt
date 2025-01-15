package com.fredy.data.database


import com.fredy.domain.model.Budget
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

interface BudgetDataSource {
    suspend fun upsertBudgetItem(budget: Budget)
    suspend fun deleteBudgetItem(budget: Budget)
    suspend fun getBudget(budgetId: String): Budget
    suspend fun getUserCategoriesOrderedByName(userId: String): Flow<List<Budget>>
}


class BudgetDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : BudgetDataSource {
    private val budgetCollection = firestore.collection(
        "budget"
    )

    override suspend fun upsertBudgetItem(
        budget: Budget
    ) {
        budgetCollection.document(
            budget.budgetId
        ).set(
            budget
        )
    }

    override suspend fun deleteBudgetItem(
        budget: Budget
    ) {
        budgetCollection.document(budget.budgetId).delete()
    }

    override suspend fun getBudget(budgetId: String): Budget {
        return withContext(Dispatchers.IO) {
            try {
                budgetCollection.document(budgetId).get().await().toObject<Budget>()
                    ?: throw Exception(
                        "Budget Not Found"
                    )
            } catch (e: Exception) {
                Timber.e(
                    "Failed to get budget: ${e.message}"
                )
                throw e
            }
        }
    }

    override suspend fun getUserCategoriesOrderedByName(userId: String): Flow<List<Budget>> {
        return withContext(Dispatchers.IO) {
            try {
                val querySnapshot = budgetCollection.whereEqualTo(
                    "userIdFk",
                    userId
                ).orderBy("budgetName").snapshots()

                querySnapshot.map { it.toObjects() }
            } catch (e: Exception) {
                Timber.e(
                    "Failed to get user categories: $e"
                )
                throw e
            }
        }
    }


}