package com.fredy.mysavings.Data.Mappers

import android.util.Log
import com.fredy.mysavings.Data.APIs.CountryModels.Response.Currencies
import com.fredy.mysavings.Data.APIs.CountryModels.Response.CurrencyHelper
import com.fredy.mysavings.Data.APIs.CountryModels.Response.CurrencyInfoItem
import com.fredy.mysavings.Data.APIs.CountryModels.Response.CurrencyInfoResponse
import com.fredy.mysavings.Data.APIs.CountryModels.Response.UsableCurrencyInfoItem
import com.fredy.mysavings.Data.APIs.CurrencyModels.Response.Rates
import com.fredy.mysavings.Data.Database.Model.Currency
import com.fredy.mysavings.Data.Database.Model.CurrencyInfoCache
import com.fredy.mysavings.Util.TAG

fun List<CurrencyInfoItem>.toCurrencyInfoCache(): CurrencyInfoCache {
    return CurrencyInfoCache(
        currencyInfoItems = this
    )
}

fun CurrencyInfoCache.toCurrencyInfoItems(): List<CurrencyInfoItem> {
    return this.currencyInfoItems
}

fun CurrencyInfoResponse.toCurrencyInfoItems(): List<CurrencyInfoItem> {
    return this.requireNoNulls().toList().requireNoNulls()
}

fun List<CurrencyInfoItem>.toCurrency(rates: Rates): List<Currency> {
    return this.toUsableCurrencyInfoItem().map {
        val currencyHelper = it.currencies
        Log.e(TAG, "toCurrency1: $currencyHelper")
        val ratesValue = rates.getRateForCurrency(it.code)!!.toDouble()
        Log.e(TAG, "toCurrency2: $currencyHelper")
        Currency(
            it.code,
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


fun List<CurrencyInfoItem>.toCurrencyHelpers(): List<CurrencyHelper> {
    val currencyHelpers = mutableListOf<CurrencyHelper>()
    this.forEach {
        currencyHelpers.add(it.currencies.toList().first())
    }
    return currencyHelpers
}


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
