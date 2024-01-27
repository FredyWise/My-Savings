package com.fredy.mysavings.DI

import com.fredy.mysavings.Data.APIs.CurrencyModels.CurrencyApi
import com.fredy.mysavings.Data.Database.Dao.AccountDao
import com.fredy.mysavings.Data.Database.Dao.RecordDao
import com.fredy.mysavings.Data.Database.FirebaseDataSource.AccountDataSource
import com.fredy.mysavings.Data.Database.FirebaseDataSource.RecordDataSource
import com.fredy.mysavings.Data.Repository.AccountRepository
import com.fredy.mysavings.Data.Repository.AccountRepositoryImpl
import com.fredy.mysavings.Data.Repository.AuthRepository
import com.fredy.mysavings.Data.Repository.AuthRepositoryImpl
import com.fredy.mysavings.Data.Repository.CategoryRepository
import com.fredy.mysavings.Data.Repository.CategoryRepositoryImpl
import com.fredy.mysavings.Data.Repository.CurrencyRepository
import com.fredy.mysavings.Data.Repository.CurrencyRepositoryImpl
import com.fredy.mysavings.Data.Repository.RecordRepository
import com.fredy.mysavings.Data.Repository.RecordRepositoryImpl
import com.fredy.mysavings.Data.Repository.UserRepository
import com.fredy.mysavings.Data.Repository.UserRepositoryImpl
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
    fun provideCurrencyRepository(
        currencyApi: CurrencyApi,
        firestore: FirebaseFirestore
    ): CurrencyRepository = CurrencyRepositoryImpl(
        currencyApi, firestore
    )

    @Provides
    @Singleton
    fun provideAuthRepository(
        oneTapClient: SignInClient,
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth,
    ): AuthRepository = AuthRepositoryImpl(
        oneTapClient, firestore, firebaseAuth
    )

    @Provides
    @Singleton
    fun provideRecordRepository(
        currencyRepository: CurrencyRepository,
        recordDataSource: RecordDataSource,
        recordDao: RecordDao,
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): RecordRepository = RecordRepositoryImpl(
        currencyRepository,
        recordDataSource,
        recordDao,
        firestore,
        firebaseAuth
    )

    @Provides
    @Singleton
    fun provideAccountRepository(
        currencyRepository: CurrencyRepository,
        accountDataSource: AccountDataSource,
        accountDao: AccountDao,
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): AccountRepository = AccountRepositoryImpl(
        currencyRepository,
        accountDataSource,
        accountDao,
        firestore,
        firebaseAuth
    )

    @Provides
    @Singleton
    fun provideCategoryRepository(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): CategoryRepository = CategoryRepositoryImpl(
        firestore, firebaseAuth
    )

    @Provides
    @Singleton
    fun provideUserRepository(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): UserRepository = UserRepositoryImpl(
        firestore, firebaseAuth,
    )
}