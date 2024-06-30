package com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Data.APIs.CountryModels.Response.CurrencyInfoItem
import com.fredy.mysavings.Feature.Data.APIs.CurrencyModels.Response.Rates
import com.fredy.mysavings.Feature.Domain.Model.Currency
import com.fredy.mysavings.Feature.Domain.Repository.CurrencyRepository
import com.fredy.mysavings.Feature.Domain.Repository.UserRepository
import com.fredy.mysavings.Feature.Domain.Util.Mappers.getRateForCurrency
import com.fredy.mysavings.Feature.Domain.Util.Mappers.toUsableCurrencyInfoItem
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.fredy.mysavings.Util.Log
import com.fredy.mysavings.Feature.Data.Util.isCacheValid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class GetCurrencies(
    private val currencyRepository: CurrencyRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<Resource<List<Currency>>> {
        return flow {
            emit(Resource.Loading())
            Log.i("getCurrencies: start")
            val currentUser = userRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val cachedRates = currencyRepository.getRateResponse()
            withContext(Dispatchers.IO) {
                currencyRepository.getCurrencies(userId)
            }.collect { cachedData ->
                Log.i("getCurrencies: $cachedData")

                val result = if (isCacheValid(cachedRates.cachedTime) && cachedData.isNotEmpty()) {
                    cachedData
                } else {
                    makeCurrencies(userId)
                }.sortedBy { it.name }
                Log.i("getCurrencies: $result")
                emit(Resource.Success(result))
            }
        }.catch { e ->
            Log.e(
                "Failed to get currencies: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }


    private suspend fun makeCurrencies(userId: String): List<Currency> {
        val newCurrencies = currencyRepository.getInfo()
            .toCurrency(currencyRepository.getRateResponse().rates, userId)
        Log.i("getCurrenciesNew: $newCurrencies")
        currencyRepository.updateCurrencies(newCurrencies)
        return newCurrencies
    }

    private fun List<CurrencyInfoItem>.toCurrency(rates: Rates, userId: String): List<Currency> {
        return this.toUsableCurrencyInfoItem().map {
            val currencyHelper = it.currencies
            Log.e("toCurrency1: $currencyHelper")
            val ratesValue = rates.getRateForCurrency(it.code)!!.toDouble()
            Log.e("toCurrency2: $currencyHelper")
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
}