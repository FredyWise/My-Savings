package com.fredy.mysavings.DI

import android.content.Context
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://api.apilayer.com/exchangerates_data/latest?symbols=&base="

interface AppModule {
    val currencyApi: CurrencyApi
    val currencyRepository: CurrencyRepository
    val provideDispatchers: DispatcherProvider
    val recordRepository: RecordRepository
    val accountRepository: AccountRepository
    val categoryRepository: CategoryRepository

    fun provideAppContext(appContext: Context)
}

object AppModuleImpl : AppModule {
    private lateinit var savingsDatabase: SavingsDatabase

    override val currencyApi: CurrencyApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyApi::class.java)
    }

    override val currencyRepository: CurrencyRepository by lazy {
        CurrencyRepositoryImpl(currencyApi)
    }

    override val provideDispatchers: DispatcherProvider by lazy {
        object : DispatcherProvider {
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

    override val recordRepository by lazy {
        RecordRepositoryImpl(savingsDatabase.recordDao())
    }

    override val accountRepository by lazy {
        AccountRepositoryImpl(savingsDatabase.accountDao())
    }

    override val categoryRepository by lazy {
        CategoryRepositoryImpl(savingsDatabase.categoryDao())
    }

    override fun provideAppContext(appContext: Context) {
        savingsDatabase = SavingsDatabase.getDatabase(appContext.applicationContext)
    }
}