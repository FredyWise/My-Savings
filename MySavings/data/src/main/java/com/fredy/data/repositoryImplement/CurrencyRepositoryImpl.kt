package com.fredy.data.repositoryImplement

import androidx.lifecycle.MutableLiveData

import com.fredy.data.util.isCacheValid
import com.fredy.data.api.countryModels.CountryApi
import com.fredy.data.api.countryModels.countryDTO.CurrencyInfoItem
import com.fredy.data.api.currencyModels.CurrencyApi
import com.fredy.data.api.currencyModels.currencyDTO.CurrencyResponse
import com.fredy.data.database.dao.CurrencyCacheDao
import com.fredy.data.database.dao.CurrencyDao
import com.fredy.data.database.firestoreDataSource.CurrencyDataSource
import com.fredy.data.database.firestoreDataSource.CurrencyRatesDataSource
import com.fredy.domain.model.Currency
import com.fredy.domain.model.RatesCache
import com.fredy.domain.repository.CurrencyRepository
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.util.mappers.toCurrencyInfoItems
import com.fredy.domain.util.mappers.toRatesCache
import com.fredy.mysavings.Feature.Domain.Model.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(
    private val userRepository: UserRepository,
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
            Timber.i("updateRates: $cache")
            _cachedRates.postValue(cache)
            currencyCacheDao.upsertCurrencyCache(cache)
            currencyRatesDataSource.upsertCurrencyRates(cache)
        }
    }

    override suspend fun updateCurrency(currency: Currency) {
        withContext(Dispatchers.IO) {
            Timber.i("updateCurrency: $currency")
            currencyDataSource.upsertCurrency(currency)
            currencyDao.upsertCurrency(currency)
        }
    }


    // currency info private function

    override suspend fun getInfo(): List<CurrencyInfoItem> {
        Timber.i("getInfo: start")

        val result = withContext(Dispatchers.IO) {
            try {
                val info = _cachedCurrencyInfoResponse.value
                if (info.isNullOrEmpty()) {
                    val apiResult = getApiCurrencyInfoResponse()
                    Timber.i(
                        "getApiCurrenciesInfo: $apiResult"
                    )
                    apiResult
                } else {
                    info
                }

            } catch (e: Exception) {
                Timber.e(
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
        Timber.i("getRates: start")
        val result = withContext(Dispatchers.IO) {
            val currentUser = _currentUser.value ?: userRepository.getCurrentUser()
            currentUser?.let { _currentUser.postValue(it) }
            val currentUserId = currentUser?.firebaseUserId ?: ""
            val rates = _cachedRates.value
            if (rates != null) {
                rates!!
            } else {
                try {
                    val cachedData = getCachedRates(base + currentUserId)
                    Timber.i("getRates: $cachedData")

                    if (cachedData != null && isCacheValid(cachedData.cachedTime)) {
                        _cachedRates.postValue(cachedData)
                        Timber.i(
                            "getCachedRates: $cachedData"
                        )
                        cachedData
                    } else {
                        val apiResult = getApiRates(base)!!.toRatesCache(currentUserId)
                        _cachedRates.postValue(apiResult)
                        updateRates(apiResult)
                        Timber.i(
                            "getApiRates: $apiResult"
                        )
                        apiResult
                    }
                } catch (e: Exception) {
                    Timber.e(
                        "Failed to fetch rates: $e"
                    )
                    throw e
                }
            }
        }

        Timber.i("getRates: success")
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