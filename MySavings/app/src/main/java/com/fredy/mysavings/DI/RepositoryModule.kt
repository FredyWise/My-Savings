package com.fredy.mysavings.DI

import android.content.Context
import com.fredy.mysavings.Feature.Data.APIs.CountryModels.CountryApi
import com.fredy.mysavings.Feature.Data.APIs.CurrencyModels.CurrencyApi
import com.fredy.mysavings.Feature.Data.CSV.CSVDao
import com.fredy.mysavings.Feature.Data.Database.Dao.AccountDao
import com.fredy.mysavings.Feature.Data.Database.Dao.CategoryDao
import com.fredy.mysavings.Feature.Data.Database.Dao.CurrencyCacheDao
import com.fredy.mysavings.Feature.Data.Database.Dao.CurrencyDao
import com.fredy.mysavings.Feature.Data.Database.Dao.RecordDao
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.AccountDataSource
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.CategoryDataSource
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.CurrencyCacheDataSource
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.CurrencyDataSource
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.RecordDataSource
import com.fredy.mysavings.Feature.Domain.Repository.AccountRepository
import com.fredy.mysavings.Feature.Domain.Repository.AccountRepositoryImpl
import com.fredy.mysavings.Feature.Domain.Repository.AuthRepository
import com.fredy.mysavings.Feature.Domain.Repository.AuthRepositoryImpl
import com.fredy.mysavings.Feature.Domain.Repository.CSVRepository
import com.fredy.mysavings.Feature.Domain.Repository.CSVRepositoryImpl
import com.fredy.mysavings.Feature.Domain.Repository.CategoryRepository
import com.fredy.mysavings.Feature.Domain.Repository.CategoryRepositoryImpl
import com.fredy.mysavings.Feature.Domain.Repository.CurrencyRepository
import com.fredy.mysavings.Feature.Domain.Repository.CurrencyRepositoryImpl
import com.fredy.mysavings.Feature.Domain.Repository.RecordRepository
import com.fredy.mysavings.Feature.Domain.Repository.RecordRepositoryImpl
import com.fredy.mysavings.Feature.Domain.Repository.SettingsRepository
import com.fredy.mysavings.Feature.Domain.Repository.SettingsRepositoryImpl
import com.fredy.mysavings.Feature.Domain.Repository.SyncRepository
import com.fredy.mysavings.Feature.Domain.Repository.SyncRepositoryImpl
import com.fredy.mysavings.Feature.Domain.Repository.UserRepository
import com.fredy.mysavings.Feature.Domain.Repository.UserRepositoryImpl
import com.google.android.gms.auth.api.identity.SignInClient
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
    fun provideCurrencyRepository(
        authRepository: AuthRepository,
        currencyApi: CurrencyApi,
        countryApi: CountryApi,
        currencyCacheDataSource: CurrencyCacheDataSource,
        currencyDataSource: CurrencyDataSource,
        currencyCacheDao: CurrencyCacheDao,
        currencyInfoCacheDao: CurrencyDao,
    ): CurrencyRepository = CurrencyRepositoryImpl(
        authRepository,
        currencyApi,
        countryApi,
        currencyCacheDataSource,
        currencyDataSource,
        currencyCacheDao,
        currencyInfoCacheDao
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
        authRepository: AuthRepository,
        recordDao: RecordDao,
        firestore: FirebaseFirestore,
    ): RecordRepository = RecordRepositoryImpl(
        currencyRepository,
        authRepository,
        recordDataSource,
        recordDao,
        firestore,
    )

    @Provides
    @Singleton
    fun provideAccountRepository(
        currencyRepository: CurrencyRepository,
        authRepository: AuthRepository,
        accountDataSource: AccountDataSource,
        accountDao: AccountDao,
        firestore: FirebaseFirestore,
    ): AccountRepository = AccountRepositoryImpl(
        currencyRepository,
        authRepository,
        accountDataSource,
        accountDao,
        firestore,
    )

    @Provides
    @Singleton
    fun provideCategoryRepository(
        authRepository: AuthRepository,
        firestore: FirebaseFirestore,
        categoryDataSource: CategoryDataSource,
        categoryDao: CategoryDao,
    ): CategoryRepository = CategoryRepositoryImpl(
        authRepository, categoryDataSource, categoryDao, firestore,
    )

    @Provides
    @Singleton
    fun provideUserRepository(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): UserRepository = UserRepositoryImpl(
        firestore, firebaseAuth,
    )

    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext appContext: Context
    ): SettingsRepository = SettingsRepositoryImpl(
        appContext
    )

    @Provides
    @Singleton
    fun provideCSVRepository(
        csvDao: CSVDao,
        authRepository: AuthRepository,
        accountRepository: AccountRepository,
        recordDataSource: RecordDataSource,
        categoryRepository: CategoryRepository
    ): CSVRepository = CSVRepositoryImpl(
        csvDao, authRepository, accountRepository, recordDataSource, categoryRepository
    )

    @Provides
    @Singleton
    fun provideSyncRepository(
        accountDataSource: AccountDataSource,
        accountDao: AccountDao,
        categoryDataSource: CategoryDataSource,
        categoryDao: CategoryDao,
        recordDataSource: RecordDataSource,
        recordDao: RecordDao,
        firebaseAuth: FirebaseAuth
    ): SyncRepository = SyncRepositoryImpl(
        accountDataSource,
        accountDao,
        categoryDataSource,
        categoryDao,
        recordDataSource,
        recordDao,
        firebaseAuth,
    )
}