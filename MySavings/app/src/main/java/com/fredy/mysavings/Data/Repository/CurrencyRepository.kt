package com.fredy.mysavings.Data.Repository

import android.util.Log
import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Data.APIs.CurrencyModels.CurrencyApi
import com.fredy.mysavings.Data.APIs.CurrencyModels.Response.CurrencyResponse
import com.fredy.mysavings.Data.APIs.CurrencyModels.Response.Rates
import com.fredy.mysavings.Util.BalanceItem
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.TAG
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface CurrencyRepository {
    fun getRates(base: String): Flow<Resource<CurrencyResponse>>

    fun convertCurrency(
        amountStr: Double,
        fromCurrency: String,
        toCurrency: String,
    ): Flow<Resource<BalanceItem>>
}

class CurrencyRepositoryImpl @Inject constructor(
    private val api: CurrencyApi,
    private val firestore: FirebaseFirestore
): CurrencyRepository {
    private val currencyCollection = firestore.collection(
        "currencyCache"
    )

    private companion object {
        const val CACHE_EXPIRATION_DAYS = 1
    }

    data class CurrencyCache(
        val currencyResponse: CurrencyResponse,
        val timestamp: Timestamp
    )

    override fun convertCurrency(
        amountStr: Double,
        fromCurrency: String,
        toCurrency: String,
    ) = callbackFlow<Resource<BalanceItem>> {
        trySend(Resource.Loading())
        Log.e(
            TAG,
            "convert: $amountStr$fromCurrency"
        )

        val baseCurrency = if (fromCurrency.contains(
                "None", ignoreCase = true
            )) toCurrency else fromCurrency

        val response = getResponse(baseCurrency)
        val rates = response!!.rates
        val rate = getRateForCurrency(
            toCurrency, rates
        )?.toDouble()

        if (rate.isNotNull()) {
            trySend(
                Resource.Success(
                    BalanceItem(
                        amount = amountStr * rate!!,
                        currency = toCurrency
                    )
                )
            )
        } else {
            trySend(Resource.Error("Unexpected error"))
        }

        awaitClose {}
    }

    override fun getRates(base: String): Flow<Resource<CurrencyResponse>> {
        return flow {
            emit(Resource.Loading())

            val cachedData = getCachedRates(base)
            if (cachedData != null && isCacheValid(
                    cachedData.timestamp
                )) {
                emit(
                    Resource.Success(
                        cachedData.currencyResponse
                    )
                )
            } else {
                val result = getResponse(base)
                result?.let {
                    emit(Resource.Success(it))
                    cacheRates(base, it)
                }
            }
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }

    private suspend fun cacheRates(
        base: String,
        response: CurrencyResponse
    ) {
        val cache = CurrencyCache(
            currencyResponse = response,
            timestamp = Timestamp.now()
        )
        currencyCollection.document(
            base
        ).set(cache).await()
    }

    private fun isCacheValid(timestamp: Timestamp): Boolean {
        val expirationTime = timestamp.nanoseconds + CACHE_EXPIRATION_DAYS * 24 * 60 * 60 * 1000//one day
        return expirationTime >= System.currentTimeMillis()
    }

    private suspend fun getCachedRates(base: String): CurrencyCache? {
        val snapshot = currencyCollection.document(
            base
        ).get().await()
        return snapshot.toObject<CurrencyCache>()
    }

    private suspend fun getResponse(base: String): CurrencyResponse? {
        val response = api.getRates(base)
        val result = response.body()
        Log.i(
            TAG,
            "currencyResponse:" + result,
        )
        return result
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


