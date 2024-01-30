package com.fredy.mysavings.DI

import android.content.Context
import androidx.room.Room
import com.fredy.mysavings.Data.Database.Dao.AccountDao
import com.fredy.mysavings.Data.Database.Dao.CategoryDao
import com.fredy.mysavings.Data.Database.Dao.CurrencyCacheDao
import com.fredy.mysavings.Data.Database.Dao.RecordDao
import com.fredy.mysavings.Data.Database.Dao.UserDao
import com.fredy.mysavings.Data.Database.FirebaseDataSource.AccountDataSource
import com.fredy.mysavings.Data.Database.FirebaseDataSource.AccountDataSourceImpl
import com.fredy.mysavings.Data.Database.FirebaseDataSource.CategoryDataSource
import com.fredy.mysavings.Data.Database.FirebaseDataSource.CategoryDataSourceImpl
import com.fredy.mysavings.Data.Database.FirebaseDataSource.CurrencyDataSource
import com.fredy.mysavings.Data.Database.FirebaseDataSource.CurrencyDataSourceImpl
import com.fredy.mysavings.Data.Database.FirebaseDataSource.RecordDataSource
import com.fredy.mysavings.Data.Database.FirebaseDataSource.RecordDataSourceImpl
import com.fredy.mysavings.Data.Database.SavingsDatabase
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
    fun provideCategoryDao(savingsDatabase: SavingsDatabase): CategoryDao = savingsDatabase.categoryDao

    @Provides
    @Singleton
    fun provideUserDao(savingsDatabase: SavingsDatabase): UserDao = savingsDatabase.userDao

    @Provides
    @Singleton
    fun provideCurrencyCacheDao(savingsDatabase: SavingsDatabase): CurrencyCacheDao = savingsDatabase.currencyCache

    @Provides
    @Singleton
    fun provideRecordDataSource(firestore: FirebaseFirestore): RecordDataSource = RecordDataSourceImpl(firestore)

    @Provides
    @Singleton
    fun provideAccountDataSource(firestore: FirebaseFirestore): AccountDataSource = AccountDataSourceImpl(firestore)

    @Provides
    @Singleton
    fun provideCategoryDataSource(firestore: FirebaseFirestore): CategoryDataSource = CategoryDataSourceImpl(firestore)

    @Provides
    @Singleton
    fun provideCurrencyCacheDataSource(firestore: FirebaseFirestore): CurrencyDataSource = CurrencyDataSourceImpl(firestore)
}
