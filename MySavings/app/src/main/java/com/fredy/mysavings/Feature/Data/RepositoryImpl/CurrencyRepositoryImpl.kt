package com.fredy.mysavings.Feature.Data.RepositoryImpl

import androidx.lifecycle.MutableLiveData
import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Data.APIs.CountryModels.CountryApi
import com.fredy.mysavings.Feature.Data.APIs.CountryModels.Response.CurrencyInfoItem
import com.fredy.mysavings.Feature.Data.APIs.CurrencyModels.CurrencyApi
import com.fredy.mysavings.Feature.Data.APIs.CurrencyModels.Response.CurrencyResponse
import com.fredy.mysavings.Feature.Data.Database.Dao.CurrencyCacheDao
import com.fredy.mysavings.Feature.Data.Database.Dao.CurrencyDao
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.CurrencyDataSource
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.CurrencyRatesDataSource
import com.fredy.mysavings.Feature.Domain.Model.Currency
import com.fredy.mysavings.Feature.Domain.Model.RatesCache
import com.fredy.mysavings.Feature.Domain.Model.UserData
import com.fredy.mysavings.Feature.Domain.Repository.AuthRepository
import com.fredy.mysavings.Feature.Domain.Repository.CurrencyRepository
import com.fredy.mysavings.Util.Log
import com.fredy.mysavings.Util.Mappers.toCurrencyInfoItems
import com.fredy.mysavings.Util.Mappers.toRatesCache
import com.fredy.mysavings.Util.isCacheValid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val currencyApi: CurrencyApi,
    private val countryApi: CountryApi,
    private val currencyRatesDataSource: CurrencyRatesDataSource,
    private val currencyDataSource: CurrencyDataSource,
    private val currencyCacheDao: CurrencyCacheDao,
    private val currencyDao: CurrencyDao,
) : CurrencyRepository {
    private val _cachedRates = MutableLiveData<RatesCache?>()
    private val _currentUser = MutableLiveData<UserData>()
    private val _cachedCurrencyInfoResponse = MutableLiveData<List<CurrencyInfoItem>>()
    override suspend fun updateRates(cache: RatesCache) {
        withContext(Dispatchers.IO) {
            Log.i("updateRates: $cache")
            _cachedRates.postValue(cache)
            currencyCacheDao.upsertCurrencyCache(cache)
            currencyRatesDataSource.upsertCurrencyRates(cache)
        }
    }

    override suspend fun updateCurrency(currency: Currency) {
        withContext(Dispatchers.IO) {
            Log.i("updateCurrency: $currency")
            currencyDataSource.upsertCurrency(currency)
            currencyDao.upsertCurrency(currency)
        }
    }


    // currency info private function

    override suspend fun getInfo(): List<CurrencyInfoItem> {
        Log.i("getInfo: start")

        val result = withContext(Dispatchers.IO) {
            try {
                val info = _cachedCurrencyInfoResponse.value
                if (info.isNullOrEmpty()) {
                    val apiResult = getApiCurrencyInfoResponse()
                    Log.i(
                        "getApiCurrenciesInfo: $apiResult"
                    )
                    apiResult
                } else {
                    info
                }

            } catch (e: Exception) {
                Log.e(
                    "Failed to fetch currencies info: $e"
                )
                throw e
            }
        }
        _cachedCurrencyInfoResponse.postValue(result)
        return result
    }

    private suspend fun getApiCurrencyInfoResponse(): List<CurrencyInfoItem> {
        val response = countryApi.getCurrencyInfo()
        return response.body()!!.toCurrencyInfoItems()
    }

    // rates private functions
    override suspend fun getRateResponse(
        base: String
    ): RatesCache {
        Log.i("getRates: start")
        val result = withContext(Dispatchers.IO) {
            val cachedUser = _currentUser.value
            val currentUser =
                if (cachedUser.isNotNull()) cachedUser!! else authRepository.getCurrentUser()!!
            _currentUser.postValue(currentUser)
            val currentUserId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val rates = _cachedRates.value
            if (rates.isNotNull()) {
                rates!!
            } else {
                try {
                    val cachedData = getCachedRates(base + currentUserId)
                    Log.i("getRates: $cachedData")

                    if (cachedData.isNotNull() && isCacheValid(cachedData!!.cachedTime)) {
                        _cachedRates.postValue(cachedData)
                        Log.i(
                            "getCachedRates: $cachedData"
                        )
                        cachedData
                    } else {
                        val apiResult = getApiRates(base)!!.toRatesCache(currentUserId)
                        _cachedRates.postValue(apiResult)
                        updateRates(apiResult)
                        Log.i(
                            "getApiRates: $apiResult"
                        )
                        apiResult
                    }
                } catch (e: Exception) {
                    Log.e(
                        "Failed to fetch rates: $e"
                    )
                    throw e
                }
            }
        }

        Log.i("getRates: success")
        return result
    }

    private suspend fun getApiRates(base: String): CurrencyResponse? {
        val response = currencyApi.getRates(base)
        return response.body()
    }

    private suspend fun getCachedRates(ratesId: String): RatesCache? {// this should be able from room and firebase
        return withContext(Dispatchers.IO) {// will not be used if used currency
            currencyRatesDataSource.getCurrencyRates(ratesId)
        }
    }


    // currencies
    override suspend fun getCurrencies(userId: String): Flow<List<Currency>> {
        return flow {
            withContext(Dispatchers.IO) {
                currencyDataSource.getCurrencies(userId)
            }.collect { accounts ->
                emit(accounts)
            }
        }
    }

    override suspend fun updateCurrencies(currencies: List<Currency>) {
        currencyDao.upsertAllCurrencies(currencies)
        currencyDataSource.upsertAllCurrencyItem(currencies)
    }

}