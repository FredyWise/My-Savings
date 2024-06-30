package com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases

import com.fredy.mysavings.Feature.Data.APIs.CurrencyModels.Response.Rates
import com.fredy.mysavings.Feature.Domain.Repository.CurrencyRepository
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.fredy.mysavings.Util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetCurrencyRates(
    private val currencyRepository: CurrencyRepository
) {
    operator fun invoke(): Flow<Resource<Rates>> {
        return flow {
            emit(Resource.Loading())
            val currencyRates = currencyRepository.getRateResponse().rates
            emit(
                Resource.Success(
                    currencyRates
                )
            )
        }.catch { e ->
            Log.e(
                "Failed to convert currency: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }
}