package com.fredy.mysavings.Feature.Domain.Repository

import android.util.Log
import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Data.Database.Dao.CategoryDao
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.CategoryDataSource
import com.fredy.mysavings.Feature.Data.Database.Model.Category
import com.fredy.mysavings.Feature.Mappers.toCategoryMaps
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
    suspend fun upsertCategory(category: Category): String
    suspend fun deleteCategory(category: Category)
    fun getCategory(categoryId: String): Flow<Category>
    fun getUserCategories(userId:String): Flow<List<Category>>
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

    override suspend fun upsertCategory(category: Category): String {
        return withContext(Dispatchers.IO) {
            val currentUserId = authRepository.getCurrentUser()!!.firebaseUserId
            val tempCategory = if (category.categoryId.isEmpty()) {
                val newCategoryRef = categoryCollection.document()
                category.copy(
                    categoryId = newCategoryRef.id,
                    userIdFk = currentUserId
                )
            } else if (category.userIdFk.isEmpty()){
                category.copy(
                    userIdFk = currentUserId
                )
            } else{
                category
            }

            categoryDao.upsertCategoryItem(
                tempCategory
            )
            categoryDataSource.upsertCategoryItem(
                tempCategory
            )
            tempCategory.categoryId
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
                categoryDao.getCategory(categoryId)
            }
            emit(category)
        }
    }

    override fun getUserCategories(userId:String): Flow<List<Category>> {
        return flow {
            withContext(Dispatchers.IO) {
                categoryDataSource.getUserCategoriesOrderedByName(
                    userId
                )
            }.collect { data ->
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
                getUserCategories(
                    userId
                )
            }.collect { categories ->
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