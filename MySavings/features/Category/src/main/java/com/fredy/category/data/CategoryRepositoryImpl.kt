package com.fredy.category.data


import com.fredy.data.database.dao.CategoryDao
import com.fredy.data.database.firestoreDataSource.CategoryDataSource
import com.fredy.data.database.firestoreDataSource.RecordDataSource
import com.fredy.data.mappers.toDomainTrueRecords
import com.fredy.domain.model.Category
import com.fredy.domain.model.TrueRecord
import com.fredy.domain.repository.CategoryRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDataSource: CategoryDataSource,
    private val recordDataSource: RecordDataSource,
    private val categoryDao: CategoryDao,
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

            categoryDao.upsertCategoryItem(
                dataCategory
            )
            categoryDataSource.upsertCategoryItem(
                dataCategory
            )
            dataCategory.categoryId
        }
    }

    override suspend fun deleteCategory(category: Category) {
        withContext(Dispatchers.IO) {
            val dataCategory = category.toDataCategory()
            categoryDataSource.deleteCategoryItem(
                dataCategory
            )
            categoryDao.deleteCategoryItem(dataCategory)
        }
    }


    override fun getCategory(categoryId: String): Flow<Category> {
        return flow {
            val domainCategory = withContext(Dispatchers.IO) {
                categoryDataSource.getCategory(categoryId)
            }.toDomainCategory()
            emit(domainCategory)
        }
    }

    override fun getUserCategories(userId: String): Flow<List<Category>> {
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


    override fun getUserCategoryRecordsOrderedByDateTime(
        userId: String,
        categoryId: String,
        sortType: com.fredy.domain.enums.SortType,
    ): Flow<List<TrueRecord>> {
        Timber.i("getUserCategoryRecordsOrderedByDateTimeRepo: $userId")
        return flow {
            recordDataSource.getUserCategoryRecordsOrderedByDateTime(userId, categoryId)
                .collect { records ->
                    Timber.i("getUserCategoryRecordsOrderedByDateTimeRepo.Data: $records")
                    emit(records.toDomainTrueRecords())
                }
        }
    }
}