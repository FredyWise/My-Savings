package com.fredy.mysavings.Data.Repository

import android.util.Log
import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Data.Database.Dao.CategoryDao
import com.fredy.mysavings.Data.Database.FirebaseDataSource.CategoryDataSource
import com.fredy.mysavings.Data.Database.Model.Category
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.TAG
import com.fredy.mysavings.ViewModels.CategoryMap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface CategoryRepository {
    suspend fun upsertCategory(category: Category)
    suspend fun deleteCategory(category: Category)
    fun getCategory(categoryId: String): Flow<Category>
    fun getUserCategoriesOrderedByName(): Flow<List<Category>>
    fun getCategoryMapOrderedByName(): Flow<Resource<List<CategoryMap>>>
}

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDataSource: CategoryDataSource,
    private val categoryDao: CategoryDao,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : CategoryRepository {
    private val categoryCollection = firestore.collection(
        "category"
    )

    override suspend fun upsertCategory(category: Category) {
        withContext(Dispatchers.IO) {
            val currentUser = firebaseAuth.currentUser
            val tempCategory = if (category.categoryId.isEmpty()) {
                val newCategoryRef = categoryCollection.document()
                category.copy(
                    categoryId = newCategoryRef.id,
                    userIdFk = currentUser!!.uid
                )
            } else {
                category.copy(
                    userIdFk = currentUser!!.uid
                )
            }

            categoryDao.upsertCategoryItem(
                tempCategory
            )
            categoryDataSource.upsertCategoryItem(
                tempCategory
            )
        }
    }

    override suspend fun deleteCategory(category: Category) {
        withContext(Dispatchers.IO) {
            categoryDataSource.deleteCategoryItem(
                category
            )
            categoryDao.deleteCategoryItem(category)
        }
    }


    override fun getCategory(categoryId: String): Flow<Category> {
        return flow {
            val category = withContext(Dispatchers.IO) {
                categoryDao.getCategory(
                    categoryId
                )
            }
            emit(category)
        }
    }

    override fun getUserCategoriesOrderedByName(): Flow<List<Category>> {
        return flow {
            val currentUser = firebaseAuth.currentUser!!
            val userId = if (currentUser.isNotNull()) currentUser.uid else ""

            val data = withContext(Dispatchers.IO) {
                categoryDao.getUserCategoriesOrderedByName(
                    userId
                )
            }
            emit(data)
        }
    }

    override fun getCategoryMapOrderedByName(
    ): Flow<Resource<List<CategoryMap>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = firebaseAuth.currentUser!!
            val userId = if (currentUser.isNotNull()) currentUser.uid else ""

            val categories = withContext(Dispatchers.IO) {
                categoryDao.getUserCategoriesOrderedByName(
                    userId
                )
            }
            val data = categories.groupBy {
                it.categoryType
            }.toSortedMap().map {
                CategoryMap(
                    categoryType = it.key,
                    categories = it.value
                )
            }
            emit(Resource.Success(data))
        }.catch { e ->
            Log.i(
                TAG,
                "getCategoryMapOrderedByName.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }


}