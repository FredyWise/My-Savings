package com.fredy.mysavings.Data.Repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Data.APIs.ApiCredentials
import com.fredy.mysavings.Data.APIs.CountryModels.CountryApi
import com.fredy.mysavings.Data.APIs.CountryModels.Response.CurrencyInfoItem
import com.fredy.mysavings.Data.APIs.CurrencyModels.CurrencyApi
import com.fredy.mysavings.Data.APIs.CurrencyModels.Response.CurrencyResponse
import com.fredy.mysavings.Data.APIs.CurrencyModels.Response.Rates
import com.fredy.mysavings.Data.Database.Converter.CurrencyRatesConverter
import com.fredy.mysavings.Data.Database.Dao.CurrencyCacheDao
import com.fredy.mysavings.Data.Database.Dao.CurrencyDao
import com.fredy.mysavings.Data.Database.FirebaseDataSource.CurrencyCacheDataSource
import com.fredy.mysavings.Data.Database.FirebaseDataSource.CurrencyDataSource
import com.fredy.mysavings.Data.Database.Model.Currency
import com.fredy.mysavings.Data.Database.Model.CurrencyCache
import com.fredy.mysavings.Data.Mappers.getRateForCurrency
import com.fredy.mysavings.Data.Mappers.toCurrency
import com.fredy.mysavings.Data.Mappers.toCurrencyInfoItems
import com.fredy.mysavings.Util.BalanceItem
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.TAG
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface CurrencyRepository {
    suspend fun updateRates(currencyResponse: CurrencyResponse)
    suspend fun updateCurrency(currency: Currency)
    fun convertCurrency(
        amount: Double,
        fromCurrency: String,
        toCurrency: String,
    ): Flow<Resource<BalanceItem>>

    fun getCurrencyRates(): Flow<Resource<Rates>>
    fun getCurrencies(): Flow<Resource<List<Currency>>>

    suspend fun convertCurrencyData(
        amount: Double,
        fromCurrency: String,
        toCurrency: String,
    ): BalanceItem
}

class CurrencyRepositoryImpl @Inject constructor(
    private val currencyApi: CurrencyApi,
    private val countryApi: CountryApi,
    private val currencyCacheDataSource: CurrencyCacheDataSource,
    private val currencyDataSource: CurrencyDataSource,
    private val currencyCacheDao: CurrencyCacheDao,
    private val currencyDao: CurrencyDao,
) : CurrencyRepository {
    // this should be separated by service and repository
    private val _cachedRates = MutableLiveData<CurrencyResponse>()
    private val _cachedCurrency = MutableLiveData<List<Currency>>()
    private val _cachedCurrencyInfoResponse = MutableLiveData<List<CurrencyInfoItem>>()
    override suspend fun updateRates(currencyResponse: CurrencyResponse) {
        withContext(Dispatchers.IO) {
            val cache = CurrencyCache(
                currencyResponse = currencyResponse,
                cachedTime = Timestamp.now()
            )
            Log.i(TAG, "cacheRates: $cache")
            _cachedRates.postValue(currencyResponse)
            currencyCacheDataSource.upsertCurrencyCache(cache)
            currencyCacheDao.upsertCurrencyCache(cache)
        }
    }

    override suspend fun updateCurrency(currency: Currency) {
        withContext(Dispatchers.IO) {
            Log.i(TAG, "cacheRates: $currency")
            currencyDataSource.upsertCurrency(currency)
            currencyDao.upsertCurrency(currency)
        }
    }

    override fun convertCurrency(
        amount: Double,
        fromCurrency: String,
        toCurrency: String,
    ): Flow<Resource<BalanceItem>> {
        return flow {
            emit(Resource.Loading())
            Log.i(
                TAG,
                "convert: $amount$fromCurrency\nto: $toCurrency"
            )
            val tempFromCurrency = if (fromCurrency.contains(
                    "None", ignoreCase = true
                )
            ) toCurrency else fromCurrency

            val rates = getRates().rates
            val result = withContext(Dispatchers.IO) {
                singleBaseCurrencyConverter(
                    amount,
                    tempFromCurrency,
                    toCurrency,
                    rates
                )
            }
            emit(
                Resource.Success(
                    BalanceItem(
                        amount = result,
                        currency = toCurrency
                    )
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

    override fun getCurrencyRates(): Flow<Resource<Rates>> {
        return flow {
            emit(Resource.Loading())
            val currencyRates = getRates().rates
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

    override suspend fun convertCurrencyData(
        amount: Double,
        fromCurrency: String,
        toCurrency: String,
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
            val rates = getRates().rates

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

    // currency info private function

    private suspend fun getInfo(): List<CurrencyInfoItem> {
        Log.i(TAG, "getInfo: start")

        val result = withContext(Dispatchers.IO) {
            try {
                val info =_cachedCurrencyInfoResponse.value
                if (info.isNullOrEmpty()) {
                    val apiResult = getApiCurrencyInfoResponse()
                    Log.i(
                        TAG,
                        "getApiCurrenciesInfo: $apiResult"
                    )
                    apiResult
                }else{
                    info
                }

            } catch (e: Exception) {
                Log.e(
                    TAG, "Failed to fetch currencies info: $e"
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
    private suspend fun getRates(base: String = ApiCredentials.CurrencyModels.BASE_CURRENCY): CurrencyResponse {//we can use worker for this and maybe also for sincronizing data on room
        Log.i(TAG, "getRates: start")

        val result = withContext(Dispatchers.IO) {
            val rates = _cachedRates.value
            if (rates.isNotNull()) {
                rates!!
            } else {
                try {
                    val cachedData = getCachedRates(base)
                    Log.i(TAG, "getRates: $cachedData")

                    if (cachedData != null && isCacheValid(cachedData.cachedTime)) {
                        val cachedResult = CurrencyResponse(
                            cachedData.base,
                            cachedData.date,
                            CurrencyRatesConverter.toRates(
                                cachedData.rates
                            ),
                            cachedData.success,
                            cachedData.timestamp
                        )
                        Log.i(
                            TAG,
                            "getCachedRates: $cachedResult"
                        )
                        cachedResult
                    } else {
                        val apiResult = getApiRates(base)!!
                        updateRates(apiResult)
                        Log.i(
                            TAG,
                            "getApiRates: $apiResult"
                        )
                        apiResult
                    }
                } catch (e: Exception) {
                    Log.e(
                        TAG, "Failed to fetch rates: $e"
                    )
                    throw e
                }
            }
        }
        _cachedRates.postValue(result)
        Log.i(TAG, "getRates: success")
        return result
    }


    private suspend fun getApiRates(base: String): CurrencyResponse? {
        val response = currencyApi.getRates(base)
        return response.body()
    }

    private suspend fun getCachedRates(base: String): CurrencyCache {// this should be able from room and firebase
        return withContext(Dispatchers.IO) {
            currencyCacheDao.getCurrencyCache(
                base
            )
        }
    }

    private fun isCacheValid(timestamp: Timestamp): Boolean {
        val timestampInMilliseconds = timestamp.seconds * 1000
        val expirationTime =
            timestampInMilliseconds + ApiCredentials.CurrencyModels.CACHE_EXPIRATION_DAYS * 24 * 60 * 60 * 1000
        return expirationTime >= System.currentTimeMillis()
    }


    private fun singleBaseCurrencyConverter(
        amount: Double,
        fromCurrency: String,
        toCurrency: String,
        rates: Rates
    ): Double {
        val toUsdRate = rates.getRateForCurrency(
            toCurrency
        )?.toDouble() ?: throw IllegalArgumentException(
            "Currency '$toCurrency' not found in rates."
        )
        val fromUsdRate = rates.getRateForCurrency(
            fromCurrency
        )?.toDouble() ?: throw IllegalArgumentException(
            "Currency '$fromCurrency' not found in rates."
        )
        return amount * (toUsdRate / fromUsdRate)
    }

    override fun getCurrencies(): Flow<Resource<List<Currency>>> {
        return flow {
            Log.i(TAG, "getCurrencies: start")
            emit(Resource.Loading())
            val result = withContext(Dispatchers.IO) {
                val cachedData = getLocalCurrencies()
                Log.i(TAG, "getCurrencies: $cachedData")
                cachedData.ifEmpty {
                    val newCurrencies = makeCurrencies()
                    Log.i(TAG, "getNewCurrencies: $newCurrencies")
                    newCurrencies
                }
            }
            _cachedCurrency.postValue(result)
            emit(Resource.Success(result))
        }.catch { e ->
            Log.e(
                TAG,
                "Failed to get currencies: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }

    private suspend fun getLocalCurrencies(): List<Currency> {
        return currencyDao.getCurrencies()
    }

    private suspend fun makeCurrencies(): List<Currency> {
        return getInfo().toCurrency(getRates().rates)
    }

}


