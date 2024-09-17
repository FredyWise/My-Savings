package com.fredy.data.di

import com.fredy.domain.credentials.ApiCredentials
import com.fredy.data.api.countryModels.CountryApi
import com.fredy.data.api.currencyModels.CurrencyApi
import com.fredy.data.api.tabScannerModel.TabScannerAPI
import com.fredy.data.api.textCorrectionModels.TypeWiseApi

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
object ApiModule {

    @Provides
    @Singleton
    fun provideTabScannerApi(okHttpClient: OkHttpClient): TabScannerAPI = Retrofit.Builder()
        .baseUrl(ApiCredentials.TabScanner.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).client(
            okHttpClient
        )
        .build()
        .create(TabScannerAPI::class.java)

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
    fun provideTextCorrectionApi(okHttpClient: OkHttpClient): TypeWiseApi =
        Retrofit.Builder().baseUrl(
            ApiCredentials.TextCorrectionModels.BASE_URL
        ).addConverterFactory(GsonConverterFactory.create()).client(
            okHttpClient
        ).build().create(TypeWiseApi::class.java)
}
