package com.fredy.mysavings.Data.Repository

import android.util.Log
import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Data.Database.Dao.CategoryDao
import com.fredy.mysavings.Data.Database.FirebaseDataSource.CategoryDataSource
import com.fredy.mysavings.Data.Database.Model.Category
import com.fredy.mysavings.Data.Mappers.toCategoryMaps
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.TAG
import com.fredy.mysavings.ViewModels.CategoryMap
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
    private val authRepository: AuthRepository,
    private val categoryDataSource: CategoryDataSource,
    private val categoryDao: CategoryDao,
    private val firestore: FirebaseFirestore,
) : CategoryRepository {
    private val categoryCollection = firestore.collection(
        "category"
    )

    override suspend fun upsertCategory(category: Category) {
        withContext(Dispatchers.IO) {
            val currentUserId = authRepository.getCurrentUser()!!.firebaseUserId
            val tempCategory = if (category.categoryId.isEmpty()) {
                val newCategoryRef = categoryCollection.document()
                category.copy(
                    categoryId = newCategoryRef.id,
                    userIdFk = currentUserId
                )
            } else {
                category.copy(
                    userIdFk = currentUserId
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
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""

            withContext(Dispatchers.IO) {
                categoryDataSource.getUserCategoriesOrderedByName(
                    userId
                )
            }.collect{ data ->
                emit(data)
            }
        }
    }

    override fun getCategoryMapOrderedByName(
    ): Flow<Resource<List<CategoryMap>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""

             withContext(Dispatchers.IO) {
                categoryDataSource.getUserCategoriesOrderedByName(
                    userId
                )
            }.collect{ categories->
                val data = categories.toCategoryMaps()
                emit(Resource.Success(data))
            }
        }.catch { e ->
            Log.i(
                TAG,
                "getCategoryMapOrderedByName.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }


}