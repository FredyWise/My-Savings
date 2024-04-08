package com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases

import android.util.Log
import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Data.APIs.ApiCredentials
import com.fredy.mysavings.Feature.Data.APIs.CurrencyModels.Response.Rates
import com.fredy.mysavings.Feature.Data.Database.Model.Currency
import com.fredy.mysavings.Feature.Data.Database.Model.RatesCache
import com.fredy.mysavings.Feature.Domain.Repository.AuthRepository
import com.fredy.mysavings.Feature.Domain.Repository.CurrencyRepository
import com.fredy.mysavings.Feature.Mappers.getRateForCurrency
import com.fredy.mysavings.Feature.Mappers.toCurrency
import com.fredy.mysavings.Feature.Mappers.updateRatesUsingCode
import com.fredy.mysavings.Util.BalanceItem
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.DefaultData.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

data class CurrencyUseCases(
    val updateCurrency: UpdateCurrency,
    val getCurrencyRates: GetCurrencyRates,
    val convertCurrencyData: ConvertCurrencyData,
    val getCurrencies: GetCurrencies,
)

suspend fun CurrencyUseCases.currencyConverter(
    amount: Double, from: String, to: String
): Double {
    return this.convertCurrencyData(
        amount, from, to
    ).amount

}

class UpdateCurrency(
    private val currencyRepository: CurrencyRepository
) {
    suspend operator fun invoke(currency: Currency) {
        withContext(Dispatchers.IO) {
            currencyRepository.updateCurrency(currency)
            syncRates(currency,ApiCredentials.CurrencyModels.BASE_CURRENCY)
        }
    }

    private suspend fun syncRates(currency: Currency, base: String) {
        val response = currencyRepository.getRateResponse(base)
        val tempRates = response.copy(
            rates = response.rates.updateRatesUsingCode(
                currency.code,
                currency.value
            )
        )
        currencyRepository.updateRates(
            tempRates
        )

    }
}

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
                TAG,
                "Failed to convert currency: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }
}

class ConvertCurrencyData(
    private val currencyRepository: CurrencyRepository
) {
    suspend operator fun invoke(
        amount: Double,
        fromCurrency: String,
        toCurrency: String
    ): BalanceItem {
        Log.i(
            TAG,
            "convert: $amount$fromCurrency\nto: $toCurrency"
        )
        val tempFromCurrency = if (fromCurrency.contains(
                "None", ignoreCase = true
            )
        ) toCurrency else fromCurrency

        return try {
            val rates = currencyRepository.getRateResponse().rates

            val result = withContext(Dispatchers.IO) {
                singleBaseCurrencyConverter(
                    amount,
                    tempFromCurrency,
                    toCurrency,
                    rates
                )
            }
            BalanceItem(
                amount = result,
                currency = toCurrency
            )
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Failed to convert currency: $e"
            )
            throw e
        }
    }


    private fun singleBaseCurrencyConverter(
        amount: Double,
        fromCurrency: String,
        toCurrency: String,
        rates: Rates
    ): Double {
        val toBaseRate = rates.getRateForCurrency(
            toCurrency
        )?.toDouble() ?: throw IllegalArgumentException(
            "Currency '$toCurrency' not found in rates."
        )
        val fromBaseRate = rates.getRateForCurrency(
            fromCurrency
        )?.toDouble() ?: throw IllegalArgumentException(
            "Currency '$fromCurrency' not found in rates."
        )
        return amount * (toBaseRate / fromBaseRate)
    }
}


class GetCurrencies(
    private val currencyRepository: CurrencyRepository,
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<Resource<List<Currency>>> {
        return flow {
            emit(Resource.Loading())
            Log.i(TAG, "getCurrencies: start")
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
//            val cachedRates = currencyRepository.getRateResponse(ApiCredentials.CurrencyModels.BASE_CURRENCY)
            withContext(Dispatchers.IO) {
                currencyRepository.getCurrencies(userId)
            }.collect { cachedData ->
                Log.i(TAG, "getCurrencies: $cachedData")

                val result =
                    if (/*isCacheValid(cachedRates.cachedTime) && */cachedData.isNotEmpty()) {
                        cachedData
                    } else {
                        makeCurrencies(userId)
                    }.sortedBy { it.name }
                Log.i(TAG, "getCurrencies: $result")
                emit(Resource.Success(result))
            }
        }.catch { e ->
            Log.e(
                TAG,
                "Failed to get currencies: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }


    private suspend fun makeCurrencies(userId: String): List<Currency> {
        val newCurrencies = currencyRepository.getInfo()
            .toCurrency(currencyRepository.getRateResponse().rates, userId)
        Log.i(TAG, "getCurrenciesNew: $newCurrencies")
        currencyRepository.updateCurrencies(newCurrencies)
        return newCurrencies
    }
}