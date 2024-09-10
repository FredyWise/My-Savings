package com.fredy.domain.useCases.CurrencyUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.domain.model.Currency
import com.fredy.domain.model.Rate
import com.fredy.domain.repository.CurrencyRepository
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.util.isCacheValid
import com.fredy.domain.util.mappers.getRateForCurrency
import com.fredy.domain.util.mappers.toUsableCurrencyInfoItem
import com.fredy.domain.util.resource.DataError
import com.fredy.domain.util.resource.Resource
import com.fredy.mysavings.Feature.Data.APIs.CountryModels.Response.CurrencyInfoItem
import com.fredy.mysavings.Feature.Data.APIs.CurrencyModels.Response.Rates
import com.fredy.mysavings.Feature.Data.Util.isCacheValid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import timber.log.Timber

class GetCurrencies(
    private val currencyRepository: CurrencyRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<Resource<List<Currency>,DataError.Local>> {
        return flow<Resource<List<Currency>,DataError.Local>> {
            emit(Resource.Loading())
            Timber.i("getCurrencies: start")
            val currentUser = userRepository.getCurrentUser()
            currentUser?.let {
                val userId = currentUser?.firebaseUserId ?: ""
                val cachedRates = currencyRepository.getRateResponse()
                withContext(Dispatchers.IO) {
                    currencyRepository.getCurrencies(userId)
                }.collect { cachedData ->
                    Timber.i("getCurrencies: $cachedData")

                    val result = if (isCacheValid(cachedRates.cachedTime) && cachedData.isNotEmpty()) {
                        cachedData
                    } else {
                        makeCurrencies(userId)
                    }.sortedBy { it.name }
                    Timber.i("getCurrencies: $result")
                    emit(Resource.Success(result))
                }
            }
        }.catch { e ->
            Timber.e(
                "Failed to get currencies: $e"
            )
            emit(Resource.Error(DataError.Local.UNKNOWN))
        }
    }


    private suspend fun makeCurrencies(userId: String): List<Currency> {
        val newCurrencies = currencyRepository.getInfo()
            .toCurrency(currencyRepository.getRateResponse(), userId)
        Timber.i("getCurrenciesNew: $newCurrencies")
        currencyRepository.updateCurrencies(newCurrencies)
        return newCurrencies
    }

    private fun List<CurrencyInfoItem>.toCurrency(rates: List<Rate>, userId: String): List<Currency> {
        return this.toUsableCurrencyInfoItem().map {
            val currencyHelper = it.currencies
            Timber.e("toCurrency1: $currencyHelper")
            val ratesValue = rates.getValueFromCode(it.code)!!.toDouble()
            Timber.e("toCurrency2: $currencyHelper")
            Currency(
                it.code + userId,
                it.code,
                userId,
                currencyHelper.name,
                currencyHelper.symbol,
                ratesValue,
                it.flags.png,
                it.flags.alt
            )
        }.distinctBy { it.code }
    }


    fun List<Rate>.getValueFromCode(code: String): Double? {
        return this.find { it.code == code }?.value
    }
}