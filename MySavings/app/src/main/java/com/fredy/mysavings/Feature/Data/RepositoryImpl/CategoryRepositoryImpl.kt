package com.fredy.mysavings.Feature.Data.RepositoryImpl

import com.fredy.mysavings.Feature.Data.Database.Dao.CategoryDao
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.CategoryDataSource
import com.fredy.mysavings.Feature.Domain.Model.Category
import com.fredy.mysavings.Feature.Domain.Repository.CategoryRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDataSource: CategoryDataSource,
    private val categoryDao: CategoryDao,
    private val firestore: FirebaseFirestore,
) : CategoryRepository {
    private val categoryCollection = firestore.collection(
        "category"
    )

    override suspend fun upsertCategory(category: Category): String {
        return withContext(Dispatchers.IO) {
            val tempCategory = if (category.categoryId.isEmpty()) {
                val newCategoryRef = categoryCollection.document()
                category.copy(
                    categoryId = newCategoryRef.id,
                )
            } else {
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
                categoryDataSource.getCategory(categoryId)
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

}