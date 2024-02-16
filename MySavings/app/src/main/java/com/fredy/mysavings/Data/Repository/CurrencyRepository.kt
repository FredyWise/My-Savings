package com.fredy.mysavings.Data.Repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.fredy.mysavings.Data.APIs.ApiCredentials
import com.fredy.mysavings.Data.APIs.CurrencyModels.CurrencyApi
import com.fredy.mysavings.Data.APIs.CurrencyModels.Response.CurrencyResponse
import com.fredy.mysavings.Data.APIs.CurrencyModels.Response.Rates
import com.fredy.mysavings.Data.Database.Converter.CurrencyRatesConverter
import com.fredy.mysavings.Data.Database.Dao.CurrencyCacheDao
import com.fredy.mysavings.Data.Database.FirebaseDataSource.CurrencyDataSource
import com.fredy.mysavings.Data.Database.Model.CurrencyCache
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
    fun convertCurrency(
        amount: Double,
        fromCurrency: String,
        toCurrency: String,
    ): Flow<Resource<BalanceItem>>

    fun getRates(): Flow<Resource<CurrencyResponse>>

    suspend fun convertCurrencyData(
        amount: Double,
        fromCurrency: String,
        toCurrency: String,
    ): BalanceItem
}

class CurrencyRepositoryImpl @Inject constructor(
    private val api: CurrencyApi,
    private val currencyDataSource: CurrencyDataSource,
    private val currencyCacheDao: CurrencyCacheDao,
) : CurrencyRepository {
    // this should be separated by service and repository
    private val _cachedRates = MutableLiveData<CurrencyResponse>()
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

            var rates = _cachedRates.value?.rates
            if (rates == null) {
                rates = getRates(ApiCredentials.CurrencyModels.BASE_CURRENCY).rates
            }
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

    override fun getRates(): Flow<Resource<CurrencyResponse>> {
        return flow {
            emit(Resource.Loading())

            var currencyResponse = _cachedRates.value
            if (currencyResponse == null) {
                currencyResponse = getRates(ApiCredentials.CurrencyModels.BASE_CURRENCY)
            }
            emit(
                Resource.Success(
                    currencyResponse
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
            var rates = _cachedRates.value?.rates
            if (rates == null) {
                rates = getRates(ApiCredentials.CurrencyModels.BASE_CURRENCY).rates
            }
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


    private suspend fun getRates(base: String): CurrencyResponse {//we can use worker for this and maybe also for sincronizing data on room
        Log.i(TAG, "getRates: start")

        val result = withContext(Dispatchers.IO) {
            try {
                val cachedData = getCachedRates(base)
                Log.i(TAG, "getRates: ${cachedData}")

                if (cachedData != null && isCacheValid(
                        cachedData.cachedTime
                    )
                ) {// should be about online or offline
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
                        "getCachedRates: ${cachedResult}"
                    )
                    cachedResult
                } else {
                    val apiResult = getApiRates(base)!!
                    cacheRates(apiResult)
                    Log.i(
                        TAG,
                        "getApiRates: ${apiResult}"
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
        _cachedRates.postValue(result)
        return result
    }


    private suspend fun getApiRates(base: String): CurrencyResponse? {
        val response = api.getRates(base)
        return response.body()
    }

    private suspend fun getCachedRates(base: String): CurrencyCache {// this should be able from room and firebase
        return withContext(Dispatchers.IO) {
            currencyCacheDao.getCurrencyCache(
                base
            )
        }
    }

    private suspend fun cacheRates(
        response: CurrencyResponse
    ) {
        withContext(Dispatchers.IO) {
            val cache = CurrencyCache(
                currencyResponse = response,
                cachedTime = Timestamp.now()
            )
            Log.i(TAG, "cacheRates: $cache")
            currencyDataSource.upsertCurrencyCacheItem(
                cache
            )
            currencyCacheDao.upsertCurrencyCache(cache)
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
        val toUsdRate = getRateForCurrency(
            toCurrency, rates
        )?.toDouble() ?: throw IllegalArgumentException(
            "Currency '$toCurrency' not found in rates."
        )
        val fromUsdRate = getRateForCurrency(
            fromCurrency, rates
        )?.toDouble() ?: throw IllegalArgumentException(
            "Currency '$fromCurrency' not found in rates."
        )
        return amount * (fromUsdRate / toUsdRate)
    }


    private fun getRateForCurrency(
        currency: String, rates: Rates
    ) = when (currency) {
        "AED" -> rates.AED
        "AFN" -> rates.AFN
        "ALL" -> rates.ALL
        "AMD" -> rates.AMD
        "ANG" -> rates.ANG
        "AOA" -> rates.AOA
        "ARS" -> rates.ARS
        "AUD" -> rates.AUD
        "AWG" -> rates.AWG
        "AZN" -> rates.AZN
        "BAM" -> rates.BAM
        "BBD" -> rates.BBD
        "BDT" -> rates.BDT
        "BGN" -> rates.BGN
        "BHD" -> rates.BHD
        "BIF" -> rates.BIF
        "BMD" -> rates.BMD
        "BND" -> rates.BND
        "BOB" -> rates.BOB
        "BRL" -> rates.BRL
        "BSD" -> rates.BSD
        "BTC" -> rates.BTC
        "BTN" -> rates.BTN
        "BWP" -> rates.BWP
        "BYN" -> rates.BYN
        "BYR" -> rates.BYR
        "BZD" -> rates.BZD
        "CAD" -> rates.CAD
        "CDF" -> rates.CDF
        "CHF" -> rates.CHF
        "CLF" -> rates.CLF
        "CLP" -> rates.CLP
        "CNY" -> rates.CNY
        "COP" -> rates.COP
        "CRC" -> rates.CRC
        "CUC" -> rates.CUC
        "CUP" -> rates.CUP
        "CVE" -> rates.CVE
        "CZK" -> rates.CZK
        "DJF" -> rates.DJF
        "DKK" -> rates.DKK
        "DOP" -> rates.DOP
        "DZD" -> rates.DZD
        "EGP" -> rates.EGP
        "ERN" -> rates.ERN
        "ETB" -> rates.ETB
        "EUR" -> rates.EUR
        "FJD" -> rates.FJD
        "FKP" -> rates.FKP
        "GBP" -> rates.GBP
        "GEL" -> rates.GEL
        "GGP" -> rates.GGP
        "GHS" -> rates.GHS
        "GIP" -> rates.GIP
        "GMD" -> rates.GMD
        "GNF" -> rates.GNF
        "GTQ" -> rates.GTQ
        "GYD" -> rates.GYD
        "HKD" -> rates.HKD
        "HNL" -> rates.HNL
        "HRK" -> rates.HRK
        "HTG" -> rates.HTG
        "HUF" -> rates.HUF
        "IDR" -> rates.IDR
        "ILS" -> rates.ILS
        "IMP" -> rates.IMP
        "INR" -> rates.INR
        "IQD" -> rates.IQD
        "IRR" -> rates.IRR
        "ISK" -> rates.ISK
        "JEP" -> rates.JEP
        "JMD" -> rates.JMD
        "JOD" -> rates.JOD
        "JPY" -> rates.JPY
        "KES" -> rates.KES
        "KGS" -> rates.KGS
        "KHR" -> rates.KHR
        "KMF" -> rates.KMF
        "KPW" -> rates.KPW
        "KRW" -> rates.KRW
        "KWD" -> rates.KWD
        "KYD" -> rates.KYD
        "KZT" -> rates.KZT
        "LAK" -> rates.LAK
        "LBP" -> rates.LBP
        "LKR" -> rates.LKR
        "LRD" -> rates.LRD
        "LSL" -> rates.LSL
        "LTL" -> rates.LTL
        "LVL" -> rates.LVL
        "LYD" -> rates.LYD
        "MAD" -> rates.MAD
        "MDL" -> rates.MDL
        "MGA" -> rates.MGA
        "MKD" -> rates.MKD
        "MMK" -> rates.MMK
        "MNT" -> rates.MNT
        "MOP" -> rates.MOP
        "MRO" -> rates.MRO
        "MUR" -> rates.MUR
        "MVR" -> rates.MVR
        "MWK" -> rates.MWK
        "MXN" -> rates.MXN
        "MYR" -> rates.MYR
        "MZN" -> rates.MZN
        "NAD" -> rates.NAD
        "NGN" -> rates.NGN
        "NIO" -> rates.NIO
        "NOK" -> rates.NOK
        "NPR" -> rates.NPR
        "NZD" -> rates.NZD
        "OMR" -> rates.OMR
        "PAB" -> rates.PAB
        "PEN" -> rates.PEN
        "PGK" -> rates.PGK
        "PHP" -> rates.PHP
        "PKR" -> rates.PKR
        "PLN" -> rates.PLN
        "PYG" -> rates.PYG
        "QAR" -> rates.QAR
        "RON" -> rates.RON
        "RSD" -> rates.RSD
        "RUB" -> rates.RUB
        "RWF" -> rates.RWF
        "SAR" -> rates.SAR
        "SBD" -> rates.SBD
        "SCR" -> rates.SCR
        "SDG" -> rates.SDG
        "SEK" -> rates.SEK
        "SGD" -> rates.SGD
        "SHP" -> rates.SHP
        "SLE" -> rates.SLE
        "SLL" -> rates.SLL
        "SOS" -> rates.SOS
        "SRD" -> rates.SRD
        "STD" -> rates.STD
        "SYP" -> rates.SYP
        "SZL" -> rates.SZL
        "THB" -> rates.THB
        "TJS" -> rates.TJS
        "TMT" -> rates.TMT
        "TND" -> rates.TND
        "TOP" -> rates.TOP
        "TRY" -> rates.TRY
        "TTD" -> rates.TTD
        "TWD" -> rates.TWD
        "TZS" -> rates.TZS
        "UAH" -> rates.UAH
        "UGX" -> rates.UGX
        "USD" -> rates.USD
        "UYU" -> rates.UYU
        "UZS" -> rates.UZS
        "VEF" -> rates.VEF
        "VES" -> rates.VES
        "VND" -> rates.VND
        "VUV" -> rates.VUV
        "WST" -> rates.WST
        "XAF" -> rates.XAF
        "XAG" -> rates.XAG
        "XAU" -> rates.XAU
        "XCD" -> rates.XCD
        "XDR" -> rates.XDR
        "XOF" -> rates.XOF
        "XPF" -> rates.XPF
        "YER" -> rates.YER
        "ZAR" -> rates.ZAR
        "ZMK" -> rates.ZMK
        "ZMW" -> rates.ZMW
        "ZWL" -> rates.ZWL
        else -> null
    }
}


