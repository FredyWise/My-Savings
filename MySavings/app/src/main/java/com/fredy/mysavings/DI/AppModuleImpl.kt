package com.fredy.mysavings.DI

import android.content.Context
import androidx.room.Room
import com.fredy.mysavings.Data.APIs.CurrencyApi
import com.fredy.mysavings.Data.RoomDatabase.SavingsDatabase
import com.fredy.mysavings.Repository.AccountRepository
import com.fredy.mysavings.Repository.AccountRepositoryImpl
import com.fredy.mysavings.Repository.CategoryRepository
import com.fredy.mysavings.Repository.CategoryRepositoryImpl
import com.fredy.mysavings.Repository.CurrencyRepository
import com.fredy.mysavings.Repository.CurrencyRepositoryImpl
import com.fredy.mysavings.Repository.RecordRepository
import com.fredy.mysavings.Repository.RecordRepositoryImpl
import com.fredy.mysavings.Util.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val BASE_URL = "https://api.apilayer.com/exchangerates_data/latest?symbols=&base="

//interface AppModule {
//    val currencyApi: CurrencyApi
//    val currencyRepository: CurrencyRepository
//    val provideDispatchers: DispatcherProvider
//    val recordRepository: RecordRepository
//    val accountRepository: AccountRepository
//    val categoryRepository: CategoryRepository
//    val savingsDatabase: SavingsDatabase
//    fun provideAppContext(appContext: Context)
//}
@Module
@InstallIn(SingletonComponent::class)
object AppModuleImpl/*: AppModule*/ {

    @Provides
    @Singleton
    fun currencyApi(): CurrencyApi {
        return Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(
            GsonConverterFactory.create()
        ).build().create(CurrencyApi::class.java)
    }

    @Provides
    @Singleton
    fun currencyRepository(currencyApi: CurrencyApi): CurrencyRepository {
        return CurrencyRepositoryImpl(currencyApi)
    }

    @Provides
    @Singleton
    fun provideDispatchers(): DispatcherProvider {
        return object: DispatcherProvider {
            override val main: CoroutineDispatcher
                get() = Dispatchers.Main
            override val io: CoroutineDispatcher
                get() = Dispatchers.IO
            override val default: CoroutineDispatcher
                get() = Dispatchers.Default
            override val unconfined: CoroutineDispatcher
                get() = Dispatchers.Unconfined
        }
    }


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
    fun recordRepository(savingsDatabase: SavingsDatabase): RecordRepository {
        return RecordRepositoryImpl(
            savingsDatabase
        )
    }

    @Provides
    @Singleton
    fun accountRepository(savingsDatabase: SavingsDatabase): AccountRepository {
        return AccountRepositoryImpl(
            savingsDatabase
        )
    }

    @Provides
    @Singleton
    fun categoryRepository(savingsDatabase: SavingsDatabase): CategoryRepository {
        return CategoryRepositoryImpl(
            savingsDatabase
        )
    }

}