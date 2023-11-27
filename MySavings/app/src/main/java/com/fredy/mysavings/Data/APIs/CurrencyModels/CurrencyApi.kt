package com.fredy.mysavings.Data.APIs.CurrencyModels

import com.fredy.mysavings.Data.APIs.CurrencyModels.CurrencyResponse
import com.fredy.mysavings.Util.CURRENCY_API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {
    @GET("/exchangerates_data/latest")
    suspend fun getRates(
        @Query("base") base: String,
        @Query("apikey") apiKey: String = CURRENCY_API_KEY
    ): Response<CurrencyResponse>
}