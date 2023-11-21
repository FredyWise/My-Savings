package com.fredy.mysavings.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.APIs.CurrencyModels.Rates
import com.fredy.mysavings.Repository.CurrencyRepository
import com.fredy.mysavings.Util.DispatcherProvider
import com.fredy.mysavings.Util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Math.round
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: CurrencyRepository,
    private val dispatchers: DispatcherProvider,
): ViewModel() {

    sealed class CurrencyEvent {
        class Success(val resultText: String): CurrencyEvent()
        class Failure(val errorText: String): CurrencyEvent()
        object Loading: CurrencyEvent()
        object Empty: CurrencyEvent()
    }

    private val _conversion = MutableStateFlow<CurrencyEvent>(
        CurrencyEvent.Empty
    )
    val conversion: StateFlow<CurrencyEvent> = _conversion

    fun convert(
        amountStr: String,
        fromCurrency: String,
        toCurrency: String
    ) {
        val fromAmount = amountStr.toFloatOrNull()
        if (fromAmount == null) {
            _conversion.value = CurrencyEvent.Failure(
                "Not a valid amount"
            )
            return
        }

        viewModelScope.launch(dispatchers.io) {
            _conversion.value = CurrencyEvent.Loading
            when (val ratesResponse = repository.getRates(
                fromCurrency
            )) {
                is Resource.Error -> _conversion.value = CurrencyEvent.Failure(
                    ratesResponse.message!!
                )

                is Resource.Success -> {
                    val rates = ratesResponse.data!!.rates
                    val rate = getRateForCurrency(
                        toCurrency,
                        rates
                    )
                    if (rate == null) {
                        _conversion.value = CurrencyEvent.Failure(
                            "Unexpected error"
                        )
                    } else {
                        val convertedCurrency = round(
                            fromAmount * rate * 100
                        ) / 100
                        _conversion.value = CurrencyEvent.Success(
                            "$fromAmount $fromCurrency = $convertedCurrency $toCurrency"
                        )
                    }
                }

                else -> {}
            }
        }
    }

    private fun getRateForCurrency(
        currency: String,
        rates: Rates
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