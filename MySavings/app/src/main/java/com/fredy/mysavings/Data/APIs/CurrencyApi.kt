package com.fredy.mysavings.Data.APIs

import com.fredy.mysavings.Data.APIs.CurrencyModels.CurrencyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {
    @GET("/latest")
    suspend fun getRates(
        @Query("base") base: String
    ): Response<CurrencyResponse>
}