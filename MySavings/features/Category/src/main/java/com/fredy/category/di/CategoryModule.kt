package com.fredy.category.di

import com.fredy.category.data.CategoryRepositoryImpl
import com.fredy.category.domain.useCases.CategoryUseCases
import com.fredy.category.domain.useCases.DeleteCategory
import com.fredy.category.domain.useCases.GetCategory
import com.fredy.category.domain.useCases.GetCategoryMapOrderedByName
import com.fredy.category.domain.useCases.GetUserCategoryRecordsOrderedByDateTime
import com.fredy.category.domain.useCases.UpdateRecordItemWithDeletedCategory
import com.fredy.category.domain.useCases.UpsertCategory
import com.fredy.data.database.dao.CategoryDao
import com.fredy.data.database.firestoreDataSource.CategoryDataSource
import com.fredy.data.database.firestoreDataSource.RecordDataSource
import com.fredy.domain.repository.CategoryRepository
import com.fredy.domain.repository.RecordRepository
import com.fredy.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CategoryModule {

//    @Provides
//    @Singleton
//    fun provideCategoryRepository(
//        firestore: FirebaseFirestore,
//        categoryDataSource: CategoryDataSource,
//        recordDataSource: RecordDataSource,
//        categoryDao: CategoryDao,
//    ): CategoryRepository = CategoryRepositoryImpl(
//        categoryDataSource, recordDataSource, categoryDao, firestore,
//    )


    @Provides
    @Singleton
    fun provideCategoryUseCases(
        userRepository: UserRepository,
        categoryRepository: CategoryRepository,
        recordRepository: RecordRepository
    ): CategoryUseCases = CategoryUseCases(
        upsertCategory = UpsertCategory(categoryRepository, userRepository),
        deleteCategory = DeleteCategory(categoryRepository),
        getCategory = GetCategory(categoryRepository),
        getCategoryMapOrderedByName = GetCategoryMapOrderedByName(
            categoryRepository,
            userRepository
        ),
        getUserCategoryRecordsOrderedByDateTime = GetUserCategoryRecordsOrderedByDateTime(
            categoryRepository,
            userRepository
        ),
        updateRecordItemWithDeletedCategory = UpdateRecordItemWithDeletedCategory(
            recordRepository,
            userRepository
        ),
    )

}