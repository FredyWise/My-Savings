package com.fredy.mysavings.Feature.Data.APIs.CountryModels

import com.fredy.mysavings.Feature.Data.APIs.ApiCredentials
import com.fredy.mysavings.Feature.Data.APIs.CountryModels.Response.CountriesResponse
import com.fredy.mysavings.Feature.Data.APIs.CountryModels.Response.CurrencyInfoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CountryApi {
    @GET(ApiCredentials.CountryModels.GET_ALL_COUNTRY)
    suspend fun getCountries(): Response<CountriesResponse>
    @GET(ApiCredentials.CountryModels.GET_ALL_COUNTRY)
    suspend fun getCurrencyInfo(
        @Query("fields") fields: String = ApiCredentials.CountryModels.GET_ALL_CURRENCY_INFO,
    ): Response<CurrencyInfoResponse>
}