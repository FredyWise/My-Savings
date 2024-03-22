package com.fredy.mysavings.Data.Mappers

import android.util.Log
import com.fredy.mysavings.Data.APIs.CountryModels.Response.Currencies
import com.fredy.mysavings.Data.APIs.CountryModels.Response.CurrencyHelper
import com.fredy.mysavings.Data.APIs.CountryModels.Response.CurrencyInfoItem
import com.fredy.mysavings.Data.APIs.CountryModels.Response.CurrencyInfoResponse
import com.fredy.mysavings.Data.APIs.CountryModels.Response.UsableCurrencyInfoItem
import com.fredy.mysavings.Data.APIs.CurrencyModels.Response.CurrencyResponse
import com.fredy.mysavings.Data.APIs.CurrencyModels.Response.Rates
import com.fredy.mysavings.Data.Database.Converter.CurrencyRatesConverter
import com.fredy.mysavings.Data.Database.Model.Currency
import com.fredy.mysavings.Data.Database.Model.RatesCache
import com.fredy.mysavings.Util.TAG


fun CurrencyInfoResponse.toCurrencyInfoItems(): List<CurrencyInfoItem> {
    return this.requireNoNulls().toList().requireNoNulls()
}

fun List<CurrencyInfoItem>.toCurrency(rates: Rates, userId: String): List<Currency> {
    return this.toUsableCurrencyInfoItem().map {
        val currencyHelper = it.currencies
        Log.e(TAG, "toCurrency1: $currencyHelper")
        val ratesValue = rates.getRateForCurrency(it.code)!!.toDouble()
        Log.e(TAG, "toCurrency2: $currencyHelper")
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

fun List<CurrencyInfoItem>.toUsableCurrencyInfoItem(): List<UsableCurrencyInfoItem> {
    return this.mapNotNull {
        if (it.currencies.toList().isNotEmpty()) {
            val currency = it.currencies.toList().first()
            Log.e(TAG, "toCurrency: $currency")
            Log.e(TAG, "toCurrency: ${it.currencies.getCurrencyCode(currency).toString()}")
            UsableCurrencyInfoItem(
                it.currencies.getCurrencyCode(currency).toString(),
                currency,
                it.flags
            )
        } else {
            null
        }
    }
}

fun List<Currency>.changeBase(newBaseCode: String): List<Currency> {
    val newBase = this.first { it.code == newBaseCode }
    return this.map { currency ->
        currency.copy(value = currency.value / newBase.value)
    }
}


fun RatesCache.toCurrencyResponse(): CurrencyResponse = CurrencyResponse(
    this.base,
    this.date,
    CurrencyRatesConverter.toRates(
        this.rates
    ),
    this.success,
    this.timestamp
)

fun Rates.getRateForCurrency(
    currency: String
) = when (currency) {
    "AED" -> this.AED
    "AFN" -> this.AFN
    "ALL" -> this.ALL
    "AMD" -> this.AMD
    "ANG" -> this.ANG
    "AOA" -> this.AOA
    "ARS" -> this.ARS
    "AUD" -> this.AUD
    "AWG" -> this.AWG
    "AZN" -> this.AZN
    "BAM" -> this.BAM
    "BBD" -> this.BBD
    "BDT" -> this.BDT
    "BGN" -> this.BGN
    "BHD" -> this.BHD
    "BIF" -> this.BIF
    "BMD" -> this.BMD
    "BND" -> this.BND
    "BOB" -> this.BOB
    "BRL" -> this.BRL
    "BSD" -> this.BSD
    "BTC" -> this.BTC
    "BTN" -> this.BTN
    "BWP" -> this.BWP
    "BYN" -> this.BYN
    "BYR" -> this.BYR
    "BZD" -> this.BZD
    "CAD" -> this.CAD
    "CDF" -> this.CDF
    "CHF" -> this.CHF
    "CLF" -> this.CLF
    "CLP" -> this.CLP
    "CNY" -> this.CNY
    "COP" -> this.COP
    "CRC" -> this.CRC
    "CUC" -> this.CUC
    "CUP" -> this.CUP
    "CVE" -> this.CVE
    "CZK" -> this.CZK
    "DJF" -> this.DJF
    "DKK" -> this.DKK
    "DOP" -> this.DOP
    "DZD" -> this.DZD
    "EGP" -> this.EGP
    "ERN" -> this.ERN
    "ETB" -> this.ETB
    "EUR" -> this.EUR
    "FJD" -> this.FJD
    "FKP" -> this.FKP
    "GBP" -> this.GBP
    "GEL" -> this.GEL
    "GGP" -> this.GGP
    "GHS" -> this.GHS
    "GIP" -> this.GIP
    "GMD" -> this.GMD
    "GNF" -> this.GNF
    "GTQ" -> this.GTQ
    "GYD" -> this.GYD
    "HKD" -> this.HKD
    "HNL" -> this.HNL
    "HRK" -> this.HRK
    "HTG" -> this.HTG
    "HUF" -> this.HUF
    "IDR" -> this.IDR
    "ILS" -> this.ILS
    "IMP" -> this.IMP
    "INR" -> this.INR
    "IQD" -> this.IQD
    "IRR" -> this.IRR
    "ISK" -> this.ISK
    "JEP" -> this.JEP
    "JMD" -> this.JMD
    "JOD" -> this.JOD
    "JPY" -> this.JPY
    "KES" -> this.KES
    "KGS" -> this.KGS
    "KHR" -> this.KHR
    "KMF" -> this.KMF
    "KPW" -> this.KPW
    "KRW" -> this.KRW
    "KWD" -> this.KWD
    "KYD" -> this.KYD
    "KZT" -> this.KZT
    "LAK" -> this.LAK
    "LBP" -> this.LBP
    "LKR" -> this.LKR
    "LRD" -> this.LRD
    "LSL" -> this.LSL
    "LTL" -> this.LTL
    "LVL" -> this.LVL
    "LYD" -> this.LYD
    "MAD" -> this.MAD
    "MDL" -> this.MDL
    "MGA" -> this.MGA
    "MKD" -> this.MKD
    "MMK" -> this.MMK
    "MNT" -> this.MNT
    "MOP" -> this.MOP
    "MRO" -> this.MRO
    "MUR" -> this.MUR
    "MVR" -> this.MVR
    "MWK" -> this.MWK
    "MXN" -> this.MXN
    "MYR" -> this.MYR
    "MZN" -> this.MZN
    "NAD" -> this.NAD
    "NGN" -> this.NGN
    "NIO" -> this.NIO
    "NOK" -> this.NOK
    "NPR" -> this.NPR
    "NZD" -> this.NZD
    "OMR" -> this.OMR
    "PAB" -> this.PAB
    "PEN" -> this.PEN
    "PGK" -> this.PGK
    "PHP" -> this.PHP
    "PKR" -> this.PKR
    "PLN" -> this.PLN
    "PYG" -> this.PYG
    "QAR" -> this.QAR
    "RON" -> this.RON
    "RSD" -> this.RSD
    "RUB" -> this.RUB
    "RWF" -> this.RWF
    "SAR" -> this.SAR
    "SBD" -> this.SBD
    "SCR" -> this.SCR
    "SDG" -> this.SDG
    "SEK" -> this.SEK
    "SGD" -> this.SGD
    "SHP" -> this.SHP
    "SLE" -> this.SLE
    "SLL" -> this.SLL
    "SOS" -> this.SOS
    "SRD" -> this.SRD
    "STD" -> this.STD
    "SYP" -> this.SYP
    "SZL" -> this.SZL
    "THB" -> this.THB
    "TJS" -> this.TJS
    "TMT" -> this.TMT
    "TND" -> this.TND
    "TOP" -> this.TOP
    "TRY" -> this.TRY
    "TTD" -> this.TTD
    "TWD" -> this.TWD
    "TZS" -> this.TZS
    "UAH" -> this.UAH
    "UGX" -> this.UGX
    "USD" -> this.USD
    "UYU" -> this.UYU
    "UZS" -> this.UZS
    "VEF" -> this.VEF
    "VES" -> this.VES
    "VND" -> this.VND
    "VUV" -> this.VUV
    "WST" -> this.WST
    "XAF" -> this.XAF
    "XAG" -> this.XAG
    "XAU" -> this.XAU
    "XCD" -> this.XCD
    "XDR" -> this.XDR
    "XOF" -> this.XOF
    "XPF" -> this.XPF
    "YER" -> this.YER
    "ZAR" -> this.ZAR
    "ZMK" -> this.ZMK
    "ZMW" -> this.ZMW
    "ZWL" -> this.ZWL
    else -> null
}

fun Rates.updateRatesUsingCode(
    code: String, value: Number
) = when (code) {
    "AED" -> this.copy(AED = value)
    "AFN" -> this.copy(AFN = value)
    "ALL" -> this.copy(ALL = value)
    "AMD" -> this.copy(AMD = value)
    "ANG" -> this.copy(ANG = value)
    "AOA" -> this.copy(AOA = value)
    "ARS" -> this.copy(ARS = value)
    "AUD" -> this.copy(AUD = value)
    "AWG" -> this.copy(AWG = value)
    "AZN" -> this.copy(AZN = value)
    "BAM" -> this.copy(BAM = value)
    "BBD" -> this.copy(BBD = value)
    "BDT" -> this.copy(BDT = value)
    "BGN" -> this.copy(BGN = value)
    "BHD" -> this.copy(BHD = value)
    "BIF" -> this.copy(BIF = value)
    "BMD" -> this.copy(BMD = value)
    "BND" -> this.copy(BND = value)
    "BOB" -> this.copy(BOB = value)
    "BRL" -> this.copy(BRL = value)
    "BSD" -> this.copy(BSD = value)
    "BTC" -> this.copy(BTC = value)
    "BTN" -> this.copy(BTN = value)
    "BWP" -> this.copy(BWP = value)
    "BYN" -> this.copy(BYN = value)
    "BYR" -> this.copy(BYR = value)
    "BZD" -> this.copy(BZD = value)
    "CAD" -> this.copy(CAD = value)
    "CDF" -> this.copy(CDF = value)
    "CHF" -> this.copy(CHF = value)
    "CLF" -> this.copy(CLF = value)
    "CLP" -> this.copy(CLP = value)
    "CNY" -> this.copy(CNY = value)
    "COP" -> this.copy(COP = value)
    "CRC" -> this.copy(CRC = value)
    "CUC" -> this.copy(CUC = value)
    "CUP" -> this.copy(CUP = value)
    "CVE" -> this.copy(CVE = value)
    "CZK" -> this.copy(CZK = value)
    "DJF" -> this.copy(DJF = value)
    "DKK" -> this.copy(DKK = value)
    "DOP" -> this.copy(DOP = value)
    "DZD" -> this.copy(DZD = value)
    "EGP" -> this.copy(EGP = value)
    "ERN" -> this.copy(ERN = value)
    "ETB" -> this.copy(ETB = value)
    "EUR" -> this.copy(EUR = value)
    "FJD" -> this.copy(FJD = value)
    "FKP" -> this.copy(FKP = value)
    "GBP" -> this.copy(GBP = value)
    "GEL" -> this.copy(GEL = value)
    "GGP" -> this.copy(GGP = value)
    "GHS" -> this.copy(GHS = value)
    "GIP" -> this.copy(GIP = value)
    "GMD" -> this.copy(GMD = value)
    "GNF" -> this.copy(GNF = value)
    "GTQ" -> this.copy(GTQ = value)
    "GYD" -> this.copy(GYD = value)
    "HKD" -> this.copy(HKD = value)
    "HNL" -> this.copy(HNL = value)
    "HRK" -> this.copy(HRK = value)
    "HTG" -> this.copy(HTG = value)
    "HUF" -> this.copy(HUF = value)
    "IDR" -> this.copy(IDR = value)
    "ILS" -> this.copy(ILS = value)
    "IMP" -> this.copy(IMP = value)
    "INR" -> this.copy(INR = value)
    "IQD" -> this.copy(IQD = value)
    "IRR" -> this.copy(IRR = value)
    "ISK" -> this.copy(ISK = value)
    "JEP" -> this.copy(JEP = value)
    "JMD" -> this.copy(JMD = value)
    "JOD" -> this.copy(JOD = value)
    "JPY" -> this.copy(JPY = value)
    "KES" -> this.copy(KES = value)
    "KGS" -> this.copy(KGS = value)
    "KHR" -> this.copy(KHR = value)
    "KMF" -> this.copy(KMF = value)
    "KPW" -> this.copy(KPW = value)
    "KRW" -> this.copy(KRW = value)
    "KWD" -> this.copy(KWD = value)
    "KYD" -> this.copy(KYD = value)
    "KZT" -> this.copy(KZT = value)
    "LAK" -> this.copy(LAK = value)
    "LBP" -> this.copy(LBP = value)
    "LKR" -> this.copy(LKR = value)
    "LRD" -> this.copy(LRD = value)
    "LSL" -> this.copy(LSL = value)
    "LTL" -> this.copy(LTL = value)
    "LVL" -> this.copy(LVL = value)
    "LYD" -> this.copy(LYD = value)
    "MAD" -> this.copy(MAD = value)
    "MDL" -> this.copy(MDL = value)
    "MGA" -> this.copy(MGA = value)
    "MKD" -> this.copy(MKD = value)
    "MMK" -> this.copy(MMK = value)
    "MNT" -> this.copy(MNT = value)
    "MOP" -> this.copy(MOP = value)
    "MRO" -> this.copy(MRO = value)
    "MUR" -> this.copy(MUR = value)
    "MVR" -> this.copy(MVR = value)
    "MWK" -> this.copy(MWK = value)
    "MXN" -> this.copy(MXN = value)
    "MYR" -> this.copy(MYR = value)
    "MZN" -> this.copy(MZN = value)
    "NAD" -> this.copy(NAD = value)
    "NGN" -> this.copy(NGN = value)
    "NIO" -> this.copy(NIO = value)
    "NOK" -> this.copy(NOK = value)
    "NPR" -> this.copy(NPR = value)
    "NZD" -> this.copy(NZD = value)
    "OMR" -> this.copy(OMR = value)
    "PAB" -> this.copy(PAB = value)
    "PEN" -> this.copy(PEN = value)
    "PGK" -> this.copy(PGK = value)
    "PHP" -> this.copy(PHP = value)
    "PKR" -> this.copy(PKR = value)
    "PLN" -> this.copy(PLN = value)
    "PYG" -> this.copy(PYG = value)
    "QAR" -> this.copy(QAR = value)
    "RON" -> this.copy(RON = value)
    "RSD" -> this.copy(RSD = value)
    "RUB" -> this.copy(RUB = value)
    "RWF" -> this.copy(RWF = value)
    "SAR" -> this.copy(SAR = value)
    "SBD" -> this.copy(SBD = value)
    "SCR" -> this.copy(SCR = value)
    "SDG" -> this.copy(SDG = value)
    "SEK" -> this.copy(SEK = value)
    "SGD" -> this.copy(SGD = value)
    "SHP" -> this.copy(SHP = value)
    "SLE" -> this.copy(SLE = value)
    "SLL" -> this.copy(SLL = value)
    "SOS" -> this.copy(SOS = value)
    "SRD" -> this.copy(SRD = value)
    "STD" -> this.copy(STD = value)
    "SYP" -> this.copy(SYP = value)
    "SZL" -> this.copy(SZL = value)
    "THB" -> this.copy(THB = value)
    "TJS" -> this.copy(TJS = value)
    "TMT" -> this.copy(TMT = value)
    "TND" -> this.copy(TND = value)
    "TOP" -> this.copy(TOP = value)
    "TRY" -> this.copy(TRY = value)
    "TTD" -> this.copy(TTD = value)
    "TWD" -> this.copy(TWD = value)
    "TZS" -> this.copy(TZS = value)
    "UAH" -> this.copy(UAH = value)
    "UGX" -> this.copy(UGX = value)
    "USD" -> this.copy(USD = value)
    "UYU" -> this.copy(UYU = value)
    "UZS" -> this.copy(UZS = value)
    "VEF" -> this.copy(VEF = value)
    "VES" -> this.copy(VES = value)
    "VND" -> this.copy(VND = value)
    "VUV" -> this.copy(VUV = value)
    "WST" -> this.copy(WST = value)
    "XAF" -> this.copy(XAF = value)
    "XAG" -> this.copy(XAG = value)
    "XAU" -> this.copy(XAU = value)
    "XCD" -> this.copy(XCD = value)
    "XDR" -> this.copy(XDR = value)
    "XOF" -> this.copy(XOF = value)
    "XPF" -> this.copy(XPF = value)
    "YER" -> this.copy(YER = value)
    "ZAR" -> this.copy(ZAR = value)
    "ZMK" -> this.copy(ZMK = value)
    "ZMW" -> this.copy(ZMW = value)
    "ZWL" -> this.copy(ZWL = value)
    else -> this
}

fun Currencies.toList(): List<CurrencyHelper> {
    return listOf(
        this.AED,
        this.AFN,
        this.ALL,
        this.AMD,
        this.ANG,
        this.AOA,
        this.ARS,
        this.AUD,
        this.AWG,
        this.AZN,
        this.BAM,
        this.BBD,
        this.BDT,
        this.BGN,
        this.BHD,
        this.BIF,
        this.BMD,
        this.BND,
        this.BOB,
        this.BRL,
        this.BSD,
//            this.BTC,
        this.BTN,
        this.BWP,
        this.BYN,
//            this.BYR,
        this.BZD,
        this.CAD,
        this.CDF,
        this.CHF,
//            this.CLF,
        this.CLP,
        this.CNY,
        this.COP,
        this.CRC,
        this.CUC,
        this.CUP,
        this.CVE,
        this.CZK,
        this.DJF,
        this.DKK,
        this.DOP,
        this.DZD,
        this.EGP,
        this.ERN,
        this.ETB,
        this.EUR,
        this.FJD,
        this.FKP,
        this.GBP,
        this.GEL,
        this.GGP,
        this.GHS,
        this.GIP,
        this.GMD,
        this.GNF,
        this.GTQ,
        this.GYD,
        this.HKD,
        this.HNL,
//            this.HRK,
        this.HTG,
        this.HUF,
        this.IDR,
        this.ILS,
        this.IMP,
        this.INR,
        this.IQD,
        this.IRR,
        this.ISK,
        this.JEP,
        this.JMD,
        this.JOD,
        this.JPY,
        this.KES,
        this.KGS,
        this.KHR,
        this.KMF,
        this.KPW,
        this.KRW,
        this.KWD,
        this.KYD,
        this.KZT,
        this.LAK,
        this.LBP,
        this.LKR,
        this.LRD,
        this.LSL,
//            this.LTL,
//            this.LVL,
        this.LYD,
        this.MAD,
        this.MDL,
        this.MGA,
        this.MKD,
        this.MMK,
        this.MNT,
        this.MOP,
//            this.MRO,
        this.MUR,
        this.MVR,
        this.MWK,
        this.MXN,
        this.MYR,
        this.MZN,
        this.NAD,
        this.NGN,
        this.NIO,
        this.NOK,
        this.NPR,
        this.NZD,
        this.OMR,
        this.PAB,
        this.PEN,
        this.PGK,
        this.PHP,
        this.PKR,
        this.PLN,
        this.PYG,
        this.QAR,
        this.RON,
        this.RSD,
        this.RUB,
        this.RWF,
        this.SAR,
        this.SBD,
        this.SCR,
        this.SDG,
        this.SEK,
        this.SGD,
        this.SHP,
//            this.SLE,
        this.SLL,
        this.SOS,
        this.SRD,
//            this.STD,
        this.SYP,
        this.SZL,
        this.THB,
        this.TJS,
        this.TMT,
        this.TND,
        this.TOP,
        this.TRY,
        this.TTD,
        this.TWD,
        this.TZS,
        this.UAH,
        this.UGX,
        this.USD,
        this.UYU,
        this.UZS,
//            this.VEF,
        this.VES,
        this.VND,
        this.VUV,
        this.WST,
        this.XAF,
//            this.XAG,
//            this.XAU,
        this.XCD,
//            this.XDR,
        this.XOF,
        this.XPF,
        this.YER,
        this.ZAR,
//            this.ZMK,
        this.ZMW,
        this.ZWL,
    ).filterNotNull()
}

fun Currencies.getCurrencyInfo(
    currency: String
) = when (currency) {
    "AED" -> this.AED
    "AFN" -> this.AFN
    "ALL" -> this.ALL
    "AMD" -> this.AMD
    "ANG" -> this.ANG
    "AOA" -> this.AOA
    "ARS" -> this.ARS
    "AUD" -> this.AUD
    "AWG" -> this.AWG
    "AZN" -> this.AZN
    "BAM" -> this.BAM
    "BBD" -> this.BBD
    "BDT" -> this.BDT
    "BGN" -> this.BGN
    "BHD" -> this.BHD
    "BIF" -> this.BIF
    "BMD" -> this.BMD
    "BND" -> this.BND
    "BOB" -> this.BOB
    "BRL" -> this.BRL
    "BSD" -> this.BSD
    "BTN" -> this.BTN
    "BWP" -> this.BWP
    "BYN" -> this.BYN
    "BZD" -> this.BZD
    "CAD" -> this.CAD
    "CDF" -> this.CDF
    "CHF" -> this.CHF
    "CLP" -> this.CLP
    "CNY" -> this.CNY
    "COP" -> this.COP
    "CRC" -> this.CRC
    "CUC" -> this.CUC
    "CUP" -> this.CUP
    "CVE" -> this.CVE
    "CZK" -> this.CZK
    "DJF" -> this.DJF
    "DKK" -> this.DKK
    "DOP" -> this.DOP
    "DZD" -> this.DZD
    "EGP" -> this.EGP
    "ERN" -> this.ERN
    "ETB" -> this.ETB
    "EUR" -> this.EUR
    "FJD" -> this.FJD
    "FKP" -> this.FKP
    "GBP" -> this.GBP
    "GEL" -> this.GEL
    "GGP" -> this.GGP
    "GHS" -> this.GHS
    "GIP" -> this.GIP
    "GMD" -> this.GMD
    "GNF" -> this.GNF
    "GTQ" -> this.GTQ
    "GYD" -> this.GYD
    "HKD" -> this.HKD
    "HNL" -> this.HNL
    "HTG" -> this.HTG
    "HUF" -> this.HUF
    "IDR" -> this.IDR
    "ILS" -> this.ILS
    "IMP" -> this.IMP
    "INR" -> this.INR
    "IQD" -> this.IQD
    "IRR" -> this.IRR
    "ISK" -> this.ISK
    "JEP" -> this.JEP
    "JMD" -> this.JMD
    "JOD" -> this.JOD
    "JPY" -> this.JPY
    "KES" -> this.KES
    "KGS" -> this.KGS
    "KHR" -> this.KHR
    "KMF" -> this.KMF
    "KPW" -> this.KPW
    "KRW" -> this.KRW
    "KWD" -> this.KWD
    "KYD" -> this.KYD
    "KZT" -> this.KZT
    "LAK" -> this.LAK
    "LBP" -> this.LBP
    "LKR" -> this.LKR
    "LRD" -> this.LRD
    "LSL" -> this.LSL
    "LYD" -> this.LYD
    "MAD" -> this.MAD
    "MDL" -> this.MDL
    "MGA" -> this.MGA
    "MKD" -> this.MKD
    "MMK" -> this.MMK
    "MNT" -> this.MNT
    "MOP" -> this.MOP
    "MUR" -> this.MUR
    "MVR" -> this.MVR
    "MWK" -> this.MWK
    "MXN" -> this.MXN
    "MYR" -> this.MYR
    "MZN" -> this.MZN
    "NAD" -> this.NAD
    "NGN" -> this.NGN
    "NIO" -> this.NIO
    "NOK" -> this.NOK
    "NPR" -> this.NPR
    "NZD" -> this.NZD
    "OMR" -> this.OMR
    "PAB" -> this.PAB
    "PEN" -> this.PEN
    "PGK" -> this.PGK
    "PHP" -> this.PHP
    "PKR" -> this.PKR
    "PLN" -> this.PLN
    "PYG" -> this.PYG
    "QAR" -> this.QAR
    "RON" -> this.RON
    "RSD" -> this.RSD
    "RUB" -> this.RUB
    "RWF" -> this.RWF
    "SAR" -> this.SAR
    "SBD" -> this.SBD
    "SCR" -> this.SCR
    "SDG" -> this.SDG
    "SEK" -> this.SEK
    "SGD" -> this.SGD
    "SHP" -> this.SHP
    "SLL" -> this.SLL
    "SOS" -> this.SOS
    "SRD" -> this.SRD
    "SYP" -> this.SYP
    "SZL" -> this.SZL
    "THB" -> this.THB
    "TJS" -> this.TJS
    "TMT" -> this.TMT
    "TND" -> this.TND
    "TOP" -> this.TOP
    "TRY" -> this.TRY
    "TTD" -> this.TTD
    "TWD" -> this.TWD
    "TZS" -> this.TZS
    "UAH" -> this.UAH
    "UGX" -> this.UGX
    "USD" -> this.USD
    "UYU" -> this.UYU
    "UZS" -> this.UZS
    "VES" -> this.VES
    "VND" -> this.VND
    "VUV" -> this.VUV
    "WST" -> this.WST
    "XAF" -> this.XAF
    "XCD" -> this.XCD
    "XOF" -> this.XOF
    "XPF" -> this.XPF
    "YER" -> this.YER
    "ZAR" -> this.ZAR
    "ZMW" -> this.ZMW
    "ZWL" -> this.ZWL
    else -> null
}

fun Currencies.getCurrencyCode(
    currency: CurrencyHelper
) = when (currency) {
    this.AED -> "AED"
    this.AFN -> "AFN"
    this.ALL -> "ALL"
    this.AMD -> "AMD"
    this.ANG -> "ANG"
    this.AOA -> "AOA"
    this.ARS -> "ARS"
    this.AUD -> "AUD"
    this.AWG -> "AWG"
    this.AZN -> "AZN"
    this.BAM -> "BAM"
    this.BBD -> "BBD"
    this.BDT -> "BDT"
    this.BGN -> "BGN"
    this.BHD -> "BHD"
    this.BIF -> "BIF"
    this.BMD -> "BMD"
    this.BND -> "BND"
    this.BOB -> "BOB"
    this.BRL -> "BRL"
    this.BSD -> "BSD"
    this.BTN -> "BTN"
    this.BWP -> "BWP"
    this.BYN -> "BYN"
    this.BZD -> "BZD"
    this.CAD -> "CAD"
    this.CDF -> "CDF"
    this.CHF -> "CHF"
    this.CLP -> "CLP"
    this.CNY -> "CNY"
    this.COP -> "COP"
    this.CRC -> "CRC"
    this.CUC -> "CUC"
    this.CUP -> "CUP"
    this.CVE -> "CVE"
    this.CZK -> "CZK"
    this.DJF -> "DJF"
    this.DKK -> "DKK"
    this.DOP -> "DOP"
    this.DZD -> "DZD"
    this.EGP -> "EGP"
    this.ERN -> "ERN"
    this.ETB -> "ETB"
    this.EUR -> "EUR"
    this.FJD -> "FJD"
    this.FKP -> "FKP"
    this.GBP -> "GBP"
    this.GEL -> "GEL"
    this.GGP -> "GGP"
    this.GHS -> "GHS"
    this.GIP -> "GIP"
    this.GMD -> "GMD"
    this.GNF -> "GNF"
    this.GTQ -> "GTQ"
    this.GYD -> "GYD"
    this.HKD -> "HKD"
    this.HNL -> "HNL"
    this.HTG -> "HTG"
    this.HUF -> "HUF"
    this.IDR -> "IDR"
    this.ILS -> "ILS"
    this.IMP -> "IMP"
    this.INR -> "INR"
    this.IQD -> "IQD"
    this.IRR -> "IRR"
    this.ISK -> "ISK"
    this.JEP -> "JEP"
    this.JMD -> "JMD"
    this.JOD -> "JOD"
    this.JPY -> "JPY"
    this.KES -> "KES"
    this.KGS -> "KGS"
    this.KHR -> "KHR"
    this.KMF -> "KMF"
    this.KPW -> "KPW"
    this.KRW -> "KRW"
    this.KWD -> "KWD"
    this.KYD -> "KYD"
    this.KZT -> "KZT"
    this.LAK -> "LAK"
    this.LBP -> "LBP"
    this.LKR -> "LKR"
    this.LRD -> "LRD"
    this.LSL -> "LSL"
    this.LYD -> "LYD"
    this.MAD -> "MAD"
    this.MDL -> "MDL"
    this.MGA -> "MGA"
    this.MKD -> "MKD"
    this.MMK -> "MMK"
    this.MNT -> "MNT"
    this.MOP -> "MOP"
    this.MUR -> "MUR"
    this.MVR -> "MVR"
    this.MWK -> "MWK"
    this.MXN -> "MXN"
    this.MYR -> "MYR"
    this.MZN -> "MZN"
    this.NAD -> "NAD"
    this.NGN -> "NGN"
    this.NIO -> "NIO"
    this.NOK -> "NOK"
    this.NPR -> "NPR"
    this.NZD -> "NZD"
    this.OMR -> "OMR"
    this.PAB -> "PAB"
    this.PEN -> "PEN"
    this.PGK -> "PGK"
    this.PHP -> "PHP"
    this.PKR -> "PKR"
    this.PLN -> "PLN"
    this.PYG -> "PYG"
    this.QAR -> "QAR"
    this.RON -> "RON"
    this.RSD -> "RSD"
    this.RUB -> "RUB"
    this.RWF -> "RWF"
    this.SAR -> "SAR"
    this.SBD -> "SBD"
    this.SCR -> "SCR"
    this.SDG -> "SDG"
    this.SEK -> "SEK"
    this.SGD -> "SGD"
    this.SHP -> "SHP"
    this.SLL -> "SLL"
    this.SOS -> "SOS"
    this.SRD -> "SRD"
    this.SYP -> "SYP"
    this.SZL -> "SZL"
    this.THB -> "THB"
    this.TJS -> "TJS"
    this.TMT -> "TMT"
    this.TND -> "TND"
    this.TOP -> "TOP"
    this.TRY -> "TRY"
    this.TTD -> "TTD"
    this.TWD -> "TWD"
    this.TZS -> "TZS"
    this.UAH -> "UAH"
    this.UGX -> "UGX"
    this.USD -> "USD"
    this.UYU -> "UYU"
    this.UZS -> "UZS"
    this.VES -> "VES"
    this.VND -> "VND"
    this.VUV -> "VUV"
    this.WST -> "WST"
    this.XAF -> "XAF"
    this.XCD -> "XCD"
    this.XOF -> "XOF"
    this.XPF -> "XPF"
    this.YER -> "YER"
    this.ZAR -> "ZAR"
    this.ZMW -> "ZMW"
    this.ZWL -> "ZWL"
    else -> null
}
