package com.fredy.mysavings.Feature.Data.APIs.CurrencyModels

import com.fredy.mysavings.Feature.Data.APIs.ApiCredentials
import com.fredy.mysavings.Feature.Data.APIs.CurrencyModels.Response.CurrencyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {
    @GET(ApiCredentials.CurrencyModels.GET_LATEST)
    suspend fun getRates(
        @Query("base") base: String,
        @Query("apikey") apiKey: String = ApiCredentials.CurrencyModels.API_KEY
    ): Response<CurrencyResponse>
}