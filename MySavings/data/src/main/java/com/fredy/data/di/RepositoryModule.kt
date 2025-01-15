package com.fredy.data.di

import android.content.Context
import com.fredy.data.database.dao.BookDao
import com.fredy.data.database.dao.CategoryDao
import com.fredy.data.database.dao.RecordDao
import com.fredy.data.database.dao.UserDao
import com.fredy.data.database.dao.WalletDao
import com.fredy.data.database.firestoreDataSource.BookDataSource
import com.fredy.data.database.firestoreDataSource.CategoryDataSource
import com.fredy.data.database.firestoreDataSource.RecordDataSource
import com.fredy.data.database.firestoreDataSource.UserDataSource
import com.fredy.data.database.firestoreDataSource.WalletDataSource
import com.fredy.data.repositoryImplement.BookRepositoryImpl
import com.fredy.data.repositoryImplement.CategoryRepositoryImpl
import com.fredy.data.repositoryImplement.RecordRepositoryImpl
import com.fredy.data.repositoryImplement.SyncRepositoryImpl
import com.fredy.data.repositoryImplement.UserRepositoryImpl
import com.fredy.data.repositoryImplement.WalletRepositoryImpl
import com.fredy.domain.repository.BookRepository
import com.fredy.domain.repository.CategoryRepository
import com.fredy.domain.repository.RecordRepository
import com.fredy.domain.repository.SyncRepository
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.repository.WalletRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    //    @Provides
//    @Singleton
//    fun textCorrectionRepository(textCorrectionApi: TypeWiseApi): CurrencyRepository {
//        return TextCorrectionRepositoryImpl(textCorrectionApi)
//    }

    @Provides
    @Singleton
    fun provideBookRepository(
        bookDataSource: BookDataSource,
    ): BookRepository = BookRepositoryImpl(
        bookDataSource
    )


    @Provides
    @Singleton
    fun provideRecordRepository(
        recordDataSource: RecordDataSource,
        recordDao: RecordDao,
        firestore: FirebaseFirestore,
    ): RecordRepository = RecordRepositoryImpl(
        recordDataSource,
        recordDao,
        firestore,
    )

    @Provides
    @Singleton
    fun provideAccountRepository(
        walletDataSource: WalletDataSource,
        walletDao: WalletDao,
        firestore: FirebaseFirestore,
    ): WalletRepository = WalletRepositoryImpl(
        walletDataSource,
        walletDao,
        firestore,
    )

    @Provides
    @Singleton
    fun provideCategoryRepository(
        firestore: FirebaseFirestore,
        categoryDataSource: CategoryDataSource,
        categoryDao: CategoryDao,
    ): CategoryRepository = CategoryRepositoryImpl(
        categoryDataSource, categoryDao, firestore,
    )


    @Provides
    @Singleton
    fun provideUserRepository(
        firebaseAuth: FirebaseAuth,
        userDataSource: UserDataSource,
        userDao: UserDao
    ): UserRepository = UserRepositoryImpl(
        firebaseAuth, userDataSource, userDao
    )

//    @Provides
//    @Singleton
//    fun provideSettingsRepository(
//        @ApplicationContext appContext: Context
//    ): PreferencesRepository = PreferencesRepositoryImpl(
//        appContext
//    )
//
//    @Provides
//    @Singleton
//    fun provideCSVRepository(
//        csvDao: CSVDao,
//    ): CSVRepository = CSVRepositoryImpl(
//        csvDao
//    )


    @Provides
    @Singleton
    fun provideSyncRepository(
        @ApplicationContext context: Context,
        walletDataSource: WalletDataSource,
        walletDao: WalletDao,
        categoryDataSource: CategoryDataSource,
        categoryDao: CategoryDao,
        bookDataSource: BookDataSource,
        bookDao: BookDao,
        recordDataSource: RecordDataSource,
        recordDao: RecordDao,
        firebaseAuth: FirebaseAuth
    ): SyncRepository = SyncRepositoryImpl(
        context,
        walletDataSource,
        walletDao,
        categoryDataSource,
        categoryDao,
        bookDataSource,
        bookDao,
        recordDataSource,
        recordDao,
        firebaseAuth,
    )
}