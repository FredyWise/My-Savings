package com.fredy.mysavings.DI

import android.content.Context
import com.fredy.mysavings.Feature.Data.APIs.CountryModels.CountryApi
import com.fredy.mysavings.Feature.Data.APIs.CurrencyModels.CurrencyApi
import com.fredy.mysavings.Feature.Data.CSV.CSVDao
import com.fredy.mysavings.Feature.Data.Database.Dao.WalletDao
import com.fredy.mysavings.Feature.Data.Database.Dao.BookDao
import com.fredy.mysavings.Feature.Data.Database.Dao.CategoryDao
import com.fredy.mysavings.Feature.Data.Database.Dao.CurrencyCacheDao
import com.fredy.mysavings.Feature.Data.Database.Dao.CurrencyDao
import com.fredy.mysavings.Feature.Data.Database.Dao.RecordDao
import com.fredy.mysavings.Feature.Data.Database.Dao.UserDao
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.WalletDataSource
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.BookDataSource
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.CategoryDataSource
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.CurrencyDataSource
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.CurrencyRatesDataSource
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.RecordDataSource
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.UserDataSource
import com.fredy.mysavings.Feature.Domain.Repository.WalletRepository
import com.fredy.mysavings.Feature.Data.RepositoryImpl.WalletRepositoryImpl
import com.fredy.mysavings.Feature.Domain.Repository.AuthRepository
import com.fredy.mysavings.Feature.Data.RepositoryImpl.AuthRepositoryImpl
import com.fredy.mysavings.Feature.Domain.Repository.BookRepository
import com.fredy.mysavings.Feature.Data.RepositoryImpl.BookRepositoryImpl
import com.fredy.mysavings.Feature.Domain.Repository.CSVRepository
import com.fredy.mysavings.Feature.Data.RepositoryImpl.CSVRepositoryImpl
import com.fredy.mysavings.Feature.Domain.Repository.CategoryRepository
import com.fredy.mysavings.Feature.Data.RepositoryImpl.CategoryRepositoryImpl
import com.fredy.mysavings.Feature.Domain.Repository.CurrencyRepository
import com.fredy.mysavings.Feature.Data.RepositoryImpl.CurrencyRepositoryImpl
import com.fredy.mysavings.Feature.Domain.Repository.RecordRepository
import com.fredy.mysavings.Feature.Data.RepositoryImpl.RecordRepositoryImpl
import com.fredy.mysavings.Feature.Domain.Repository.SettingsRepository
import com.fredy.mysavings.Feature.Domain.Repository.SettingsRepositoryImpl
import com.fredy.mysavings.Feature.Domain.Repository.SyncRepository
import com.fredy.mysavings.Feature.Domain.Repository.SyncRepositoryImpl
import com.fredy.mysavings.Feature.Domain.Repository.UserRepository
import com.fredy.mysavings.Feature.Data.RepositoryImpl.UserRepositoryImpl
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
        currencyRatesDataSource: CurrencyRatesDataSource,
        currencyDataSource: CurrencyDataSource,
        currencyCacheDao: CurrencyCacheDao,
        currencyInfoCacheDao: CurrencyDao,
    ): CurrencyRepository = CurrencyRepositoryImpl(
        authRepository,
        currencyApi,
        countryApi,
        currencyRatesDataSource,
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
    fun provideBookRepository(
        firestore: FirebaseFirestore,
        bookDataSource: BookDataSource,
        bookDao: BookDao,
    ): BookRepository = BookRepositoryImpl(
        bookDataSource, bookDao, firestore,
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
        walletRepository: WalletRepository,
        recordDataSource: RecordDataSource,
        categoryRepository: CategoryRepository
    ): CSVRepository = CSVRepositoryImpl(
        csvDao, walletRepository, recordDataSource, categoryRepository
    )

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