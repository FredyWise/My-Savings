package com.fredy.mysavings.DI

import com.fredy.mysavings.Data.APIs.ApiCredentials
import com.fredy.mysavings.Data.APIs.CurrencyModels.CurrencyApi
import com.fredy.mytest.APIs.TextCorrectionModule.TypeWiseApi
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
    fun provideCurrencyApi(okHttpClient: OkHttpClient): CurrencyApi = Retrofit.Builder().baseUrl(
        ApiCredentials.CurrencyModels.BASE_URL
    ).addConverterFactory(GsonConverterFactory.create()).client(
            okHttpClient
        ).build().create(CurrencyApi::class.java)

    @Provides
    @Singleton
    fun provideTextCorrectionApi(okHttpClient: OkHttpClient): TypeWiseApi = Retrofit.Builder().baseUrl(
        ApiCredentials.TextCorrectionModule.BASE_URL
    ).addConverterFactory(GsonConverterFactory.create()).client(
        okHttpClient
    ).build().create(TypeWiseApi::class.java)
}
