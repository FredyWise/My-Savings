package com.fredy.data.api.currencyModels

import com.fredy.core.credentials.ApiCredentials
import com.fredy.data.api.currencyModels.currencyDTO.CurrencyResponse
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