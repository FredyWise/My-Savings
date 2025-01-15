package com.fredy.data.repository


import com.fredy.data.database.BudgetDataSource
import com.fredy.data.database.dao.CategoryDao
import com.fredy.data.database.firestoreDataSource.CategoryDataSource
import com.fredy.data.mappers.toDataCategory
import com.fredy.data.mappers.toDomainCategory
import com.fredy.domain.model.Category
import com.fredy.domain.repository.CategoryRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BudgetRepositoryImpl @Inject constructor(
    private val categoryDataSource: CategoryDataSource,
    private val budgetDataSource: BudgetDataSource,
    private val firestore: FirebaseFirestore,
) : CategoryRepository {
    private val categoryCollection = firestore.collection(
        "category"
    )

    override suspend fun upsertCategory(category: Category): String {
        return withContext(Dispatchers.IO) {
            val dataCategory = if (category.categoryId.isEmpty()) {
                val newCategoryRef = categoryCollection.document()
                category.copy(
                    categoryId = newCategoryRef.id,
                )
            } else {
                category
            }.toDataCategory()

            categoryDataSource.upsertCategoryItem(
                dataCategory
            )
            dataCategory.categoryId
        }
    }

//    override suspend fun deleteCategory(category: Category) {
//        withContext(Dispatchers.IO) {
//            val dataCategory = category.toDataCategory()
//            categoryDataSource.deleteCategoryItem(
//                dataCategory
//            )
//            categoryDao.deleteCategoryItem(dataCategory)
//        }
//    }
//
//
//    override fun getCategory(categoryId: String): Flow<Category> {
//        return flow {
//            val domainCategory = withContext(Dispatchers.IO) {
//                categoryDataSource.getCategory(categoryId)
//            }.toDomainCategory()
//            emit(domainCategory)
//        }
//    }

    override fun getUserCategories(userId:String): Flow<List<Category>> {
        return flow {
            withContext(Dispatchers.IO) {
                categoryDataSource.getUserCategoriesOrderedByName(
                    userId
                )
            }.collect { data ->
                val domainCategories = data.map {
                    it.toDomainCategory()
                }
                emit(domainCategories)
            }
        }
    }

}