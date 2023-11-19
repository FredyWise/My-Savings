package com.fredy.mysavings.Repository

import com.fredy.mysavings.Data.APIs.CurrencyApi
import com.fredy.mysavings.Data.APIs.CurrencyModels.CurrencyResponse
import com.fredy.mysavings.Util.Resource

interface CurrencyRepository {
    suspend fun getRates(base: String): Resource<CurrencyResponse>
}

class CurrencyRepositoryImpl(
    private val api: CurrencyApi
) : CurrencyRepository {
    override suspend fun getRates(base: String): Resource<CurrencyResponse> {
        return try {
            val response = api.getRates(base)
            val result = response.body()
            if(response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }
        } catch(e: Exception) {
            Resource.Error(e.message ?: "An error occured")
        }
    }
}