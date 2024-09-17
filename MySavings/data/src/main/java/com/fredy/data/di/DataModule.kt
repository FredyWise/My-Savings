package com.fredy.data.di

import android.content.Context
import androidx.room.Room
import com.fredy.data.CSV.CSVDao
import com.fredy.data.CSV.CSVDaoImpl
import com.fredy.data.database.SavingsDatabase
import com.fredy.data.database.dao.BookDao
import com.fredy.data.database.dao.CategoryDao
import com.fredy.data.database.dao.CurrencyCacheDao
import com.fredy.data.database.dao.CurrencyDao
import com.fredy.data.database.dao.RecordDao
import com.fredy.data.database.dao.UserDao
import com.fredy.data.database.dao.WalletDao
import com.fredy.data.database.firestoreDataSource.BookDataSource
import com.fredy.data.database.firestoreDataSource.BookDataSourceImpl
import com.fredy.data.database.firestoreDataSource.CategoryDataSource
import com.fredy.data.database.firestoreDataSource.CategoryDataSourceImpl
import com.fredy.data.database.firestoreDataSource.CurrencyDataSource
import com.fredy.data.database.firestoreDataSource.CurrencyDataSourceImpl
import com.fredy.data.database.firestoreDataSource.CurrencyRatesDataSource
import com.fredy.data.database.firestoreDataSource.CurrencyRatesDataSourceImpl
import com.fredy.data.database.firestoreDataSource.RecordDataSource
import com.fredy.data.database.firestoreDataSource.RecordDataSourceImpl
import com.fredy.data.database.firestoreDataSource.UserDataSource
import com.fredy.data.database.firestoreDataSource.UserDataSourceImpl
import com.fredy.data.database.firestoreDataSource.WalletDataSource
import com.fredy.data.database.firestoreDataSource.WalletDataSourceImpl
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
    fun provideAccountDao(savingsDatabase: SavingsDatabase): WalletDao = savingsDatabase.walletDao

    @Provides
    @Singleton
    fun provideCategoryDao(savingsDatabase: SavingsDatabase): CategoryDao =
        savingsDatabase.categoryDao

    @Provides
    @Singleton
    fun provideBookDao(savingsDatabase: SavingsDatabase): BookDao =
        savingsDatabase.bookDao

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
    fun provideAccountDataSource(firestore: FirebaseFirestore): WalletDataSource =
        WalletDataSourceImpl(firestore)

    @Provides
    @Singleton
    fun provideCategoryDataSource(firestore: FirebaseFirestore): CategoryDataSource =
        CategoryDataSourceImpl(firestore)

    @Provides
    @Singleton
    fun provideBookDataSource(firestore: FirebaseFirestore): BookDataSource =
        BookDataSourceImpl(firestore)

    @Provides
    @Singleton
    fun provideCurrencyCacheDataSource(firestore: FirebaseFirestore): CurrencyRatesDataSource =
        CurrencyRatesDataSourceImpl(firestore)

    @Provides
    @Singleton
    fun provideCurrencyDataSource(firestore: FirebaseFirestore): CurrencyDataSource =
        CurrencyDataSourceImpl(firestore)
    @Provides
    @Singleton
    fun provideUserDataSource(firestore: FirebaseFirestore): UserDataSource =
        UserDataSourceImpl(firestore)

    @Provides
    @Singleton
    fun provideCSVDao(@ApplicationContext context: Context): CSVDao = CSVDaoImpl(context)


}
