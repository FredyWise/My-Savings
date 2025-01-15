package com.fredy.currency.di

import com.fredy.currency.data.CurrencyRepositoryImpl
import com.fredy.currency.data.countryModels.CountryApi
import com.fredy.currency.data.currencyModels.CurrencyApi
import com.fredy.currency.domain.CurrencyRepository
import com.fredy.currency.domain.useCases.ConvertCurrencyData
import com.fredy.currency.domain.useCases.CurrencyUseCases
import com.fredy.currency.domain.useCases.GetCurrencies
import com.fredy.currency.domain.useCases.GetCurrencyRates
import com.fredy.currency.domain.useCases.UpdateCurrency
import com.fredy.domain.credentials.ApiCredentials

import com.fredy.data.database.dao.CurrencyCacheDao
import com.fredy.data.database.dao.CurrencyDao
import com.fredy.data.database.firestoreDataSource.CurrencyDataSource
import com.fredy.data.database.firestoreDataSource.CurrencyRatesDataSource
import com.fredy.domain.repository.UserRepository


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CurrencyModule {


    @Provides
    @Singleton
    fun provideCountryApi(okHttpClient: OkHttpClient): CountryApi = Retrofit.Builder().baseUrl(
        ApiCredentials.CountryModels.BASE_URL
    ).addConverterFactory(GsonConverterFactory.create()).client(
        okHttpClient
    ).build().create(CountryApi::class.java)

    @Provides
    @Singleton
    fun provideCurrencyApi(okHttpClient: OkHttpClient): CurrencyApi = Retrofit.Builder().baseUrl(
        ApiCredentials.CurrencyModels.BASE_URL
    ).addConverterFactory(GsonConverterFactory.create()).client(
        okHttpClient
    ).build().create(CurrencyApi::class.java)


    @Provides
    @Singleton
    fun provideCurrencyRepository(
        userRepository: UserRepository,
        currencyApi: CurrencyApi,
        countryApi: CountryApi,
        currencyRatesDataSource: CurrencyRatesDataSource,
        currencyDataSource: CurrencyDataSource,
        currencyCacheDao: CurrencyCacheDao,
        currencyInfoCacheDao: CurrencyDao,
    ): CurrencyRepository = CurrencyRepositoryImpl(
        userRepository,
        currencyApi,
        countryApi,
        currencyRatesDataSource,
        currencyDataSource,
        currencyCacheDao,
        currencyInfoCacheDao
    )



    @Provides
    @Singleton
    fun provideCurrencyUseCases(
        userRepository: UserRepository,
        currencyRepository: CurrencyRepository,
    ): CurrencyUseCases = CurrencyUseCases(
        updateCurrency = UpdateCurrency(currencyRepository),
        getCurrencyRates = GetCurrencyRates(currencyRepository),
        convertCurrencyData = ConvertCurrencyData(currencyRepository),
        getCurrencies = GetCurrencies(currencyRepository, userRepository)
    )

}
