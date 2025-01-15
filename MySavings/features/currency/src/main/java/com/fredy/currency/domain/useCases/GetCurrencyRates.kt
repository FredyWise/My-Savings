package com.fredy.currency.domain.useCases

import com.fredy.currency.domain.CurrencyRepository
import com.fredy.domain.model.Rate

import com.fredy.domain.util.resource.DataError
import com.fredy.domain.util.resource.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class GetCurrencyRates(
    private val currencyRepository: CurrencyRepository
) {
    operator fun invoke(): Flow<Resource<List<Rate>, DataError.Local>> {
        return flow<Resource<List<Rate>, DataError.Local>> {
            emit(Resource.Loading())
            val currencyRates = currencyRepository.getRateResponse()!!.rates
            emit(
                Resource.Success(
                    currencyRates
                )
            )
        }.catch { e ->
            Timber.e(
                "Failed to convert currency: $e"
            )
            emit(Resource.Error(DataError.Local.UNKNOWN))
        }
    }
}