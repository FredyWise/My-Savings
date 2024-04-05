package com.fredy.mysavings.DI

import android.content.Context
import androidx.room.Room
import com.fredy.mysavings.Feature.Data.CSV.CSVDao
import com.fredy.mysavings.Feature.Data.CSV.CSVDaoImpl
import com.fredy.mysavings.Feature.Data.Database.Dao.AccountDao
import com.fredy.mysavings.Feature.Data.Database.Dao.CategoryDao
import com.fredy.mysavings.Feature.Data.Database.Dao.CurrencyCacheDao
import com.fredy.mysavings.Feature.Data.Database.Dao.CurrencyDao
import com.fredy.mysavings.Feature.Data.Database.Dao.RecordDao
import com.fredy.mysavings.Feature.Data.Database.Dao.UserDao
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.AccountDataSource
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.AccountDataSourceImpl
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.CategoryDataSource
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.CategoryDataSourceImpl
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.CurrencyCacheDataSource
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.CurrencyCacheDataSourceImpl
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.CurrencyDataSource
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.CurrencyDataSourceImpl
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.RecordDataSource
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.RecordDataSourceImpl
import com.fredy.mysavings.Feature.Data.Database.SavingsDatabase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun savingsDatabase(@ApplicationContext appContext: Context): SavingsDatabase {
        return Room.databaseBuilder(
            appContext,
            SavingsDatabase::class.java,
            "savings_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideRecordDao(savingsDatabase: SavingsDatabase): RecordDao = savingsDatabase.recordDao

    @Provides
    @Singleton
    fun provideAccountDao(savingsDatabase: SavingsDatabase): AccountDao = savingsDatabase.accountDao

    @Provides
    @Singleton
    fun provideCategoryDao(savingsDatabase: SavingsDatabase): CategoryDao =
        savingsDatabase.categoryDao

    @Provides
    @Singleton
    fun provideUserDao(savingsDatabase: SavingsDatabase): UserDao = savingsDatabase.userDao

    @Provides
    @Singleton
    fun provideCurrencyCacheDao(savingsDatabase: SavingsDatabase): CurrencyCacheDao =
        savingsDatabase.currencyCache

    @Provides
    @Singleton
    fun provideCurrencyDao(savingsDatabase: SavingsDatabase): CurrencyDao =
        savingsDatabase.currency

    @Provides
    @Singleton
    fun provideRecordDataSource(firestore: FirebaseFirestore): RecordDataSource =
        RecordDataSourceImpl(firestore)

    @Provides
    @Singleton
    fun provideAccountDataSource(firestore: FirebaseFirestore): AccountDataSource =
        AccountDataSourceImpl(firestore)

    @Provides
    @Singleton
    fun provideCategoryDataSource(firestore: FirebaseFirestore): CategoryDataSource =
        CategoryDataSourceImpl(firestore)

    @Provides
    @Singleton
    fun provideCurrencyCacheDataSource(firestore: FirebaseFirestore): CurrencyCacheDataSource =
        CurrencyCacheDataSourceImpl(firestore)

    @Provides
    @Singleton
    fun provideCurrencyDataSource(firestore: FirebaseFirestore): CurrencyDataSource =
        CurrencyDataSourceImpl(firestore)

    @Provides
    @Singleton
    fun provideCSVDao(@ApplicationContext context: Context): CSVDao = CSVDaoImpl(context)


}
