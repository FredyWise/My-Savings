package com.fredy.data.mappers


import com.fredy.data.api.countryModels.countryDTO.Currencies
import com.fredy.data.api.countryModels.countryDTO.CurrencyHelper
import com.fredy.data.api.countryModels.countryDTO.CurrencyInfoItem
import com.fredy.data.api.countryModels.countryDTO.CurrencyInfoResponse
import com.fredy.data.database.dto.UsableCurrencyInfoItem
import com.fredy.data.api.currencyModels.currencyDTO.CurrencyResponse
import com.fredy.data.database.converter.CurrencyRatesDoubleConverter
import com.fredy.data.database.dto.FirebaseRatesCache
import com.fredy.domain.model.Rate
import com.fredy.domain.model.RatesCache
import com.fredy.domain.useCases.CurrencyUseCases.getValueFromCode
import com.google.firebase.Timestamp
import timber.log.Timber
import com.fredy.data.database.dto.Currency as DataCurrency
import com.fredy.domain.model.Currency as DomainCurrency
fun DomainCurrency.toDataCurrency(): DataCurrency {
    return DataCurrency(
        currencyId,
        code,
        userIdFk,
        name,
        symbol,
        value,
        url,
        alt,
    )
}

fun List<DataCurrency>.toDataCurrency(): List<DomainCurrency> {
    return this.map {
        DomainCurrency(
            it.currencyId,
            it.code,
            it.userIdFk,
            it.name,
            it.symbol,
            it.value,
            it.url,
            it.alt,
        )
    }
}

fun List<DomainCurrency>.toDataCurrency(): List<DataCurrency> {
    return this.map {
        DataCurrency(
            it.currencyId,
            it.code,
            it.userIdFk,
            it.name,
            it.symbol,
            it.value,
            it.url,
            it.alt,
        )
    }
}

fun CurrencyInfoResponse.toCurrencyInfoItems(): List<CurrencyInfoItem> {
    return this.requireNoNulls().toList().requireNoNulls()
}


fun List<CurrencyInfoItem>.toUsableCurrencyInfoItem(): List<UsableCurrencyInfoItem> {
    return this.mapNotNull {
        val currencies = it.currencies.toList()
        if (currencies.isNotEmpty()) {
            val currency = currencies.first()
            Timber.e("toCurrency: $currency")
            UsableCurrencyInfoItem(
                currency.name,
                currency,
                it.flags
            )
        } else {
            null
        }
    }
}
fun List<CurrencyInfoItem>.toCurrency(rates: List<Rate>, userId: String): List<DomainCurrency> {
    return this.toUsableCurrencyInfoItem().map {
        val currencyHelper = it.currencies
        val ratesValue = rates.getValueFromCode(it.code)
        DomainCurrency(
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


fun CurrencyResponse.toRatesCache(userId: String): RatesCache = RatesCache(
    this.base + userId,
    this.base,
    this.date,
    this.rates.toList(),
    this.success,
    this.timestamp,
    Timestamp.now(),
)

fun RatesCache.toFireBaseRatesCache(): FirebaseRatesCache {
    return FirebaseRatesCache(
        cacheId = cacheId,
        base = base,
        date = date,
        rates = CurrencyRatesDoubleConverter.fromRates(rates.toRates()),
        success = success,
        timestamp = timestamp,
        cachedTime = cachedTime
    )
}

fun FirebaseRatesCache.toRatesCache(): RatesCache {
    return RatesCache(
        cacheId = cacheId,
        base = base,
        date = date,
        rates = CurrencyRatesDoubleConverter.toRates(rates).toList(),
        success = success,
        timestamp = timestamp,
        cachedTime = cachedTime
    )
}


fun Currencies.toList(): List<CurrencyHelper> {
    return listOfNotNull(
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
    )
}
//
//fun Currencies.getCurrencyCode(
//    currency: CurrencyHelper
//) = when (currency) {
//    this.AED -> "AED"
//    this.AFN -> "AFN"
//    this.ALL -> "ALL"
//    this.AMD -> "AMD"
//    this.ANG -> "ANG"
//    this.AOA -> "AOA"
//    this.ARS -> "ARS"
//    this.AUD -> "AUD"
//    this.AWG -> "AWG"
//    this.AZN -> "AZN"
//    this.BAM -> "BAM"
//    this.BBD -> "BBD"
//    this.BDT -> "BDT"
//    this.BGN -> "BGN"
//    this.BHD -> "BHD"
//    this.BIF -> "BIF"
//    this.BMD -> "BMD"
//    this.BND -> "BND"
//    this.BOB -> "BOB"
//    this.BRL -> "BRL"
//    this.BSD -> "BSD"
//    this.BTN -> "BTN"
//    this.BWP -> "BWP"
//    this.BYN -> "BYN"
//    this.BZD -> "BZD"
//    this.CAD -> "CAD"
//    this.CDF -> "CDF"
//    this.CHF -> "CHF"
//    this.CLP -> "CLP"
//    this.CNY -> "CNY"
//    this.COP -> "COP"
//    this.CRC -> "CRC"
//    this.CUC -> "CUC"
//    this.CUP -> "CUP"
//    this.CVE -> "CVE"
//    this.CZK -> "CZK"
//    this.DJF -> "DJF"
//    this.DKK -> "DKK"
//    this.DOP -> "DOP"
//    this.DZD -> "DZD"
//    this.EGP -> "EGP"
//    this.ERN -> "ERN"
//    this.ETB -> "ETB"
//    this.EUR -> "EUR"
//    this.FJD -> "FJD"
//    this.FKP -> "FKP"
//    this.GBP -> "GBP"
//    this.GEL -> "GEL"
//    this.GGP -> "GGP"
//    this.GHS -> "GHS"
//    this.GIP -> "GIP"
//    this.GMD -> "GMD"
//    this.GNF -> "GNF"
//    this.GTQ -> "GTQ"
//    this.GYD -> "GYD"
//    this.HKD -> "HKD"
//    this.HNL -> "HNL"
//    this.HTG -> "HTG"
//    this.HUF -> "HUF"
//    this.IDR -> "IDR"
//    this.ILS -> "ILS"
//    this.IMP -> "IMP"
//    this.INR -> "INR"
//    this.IQD -> "IQD"
//    this.IRR -> "IRR"
//    this.ISK -> "ISK"
//    this.JEP -> "JEP"
//    this.JMD -> "JMD"
//    this.JOD -> "JOD"
//    this.JPY -> "JPY"
//    this.KES -> "KES"
//    this.KGS -> "KGS"
//    this.KHR -> "KHR"
//    this.KMF -> "KMF"
//    this.KPW -> "KPW"
//    this.KRW -> "KRW"
//    this.KWD -> "KWD"
//    this.KYD -> "KYD"
//    this.KZT -> "KZT"
//    this.LAK -> "LAK"
//    this.LBP -> "LBP"
//    this.LKR -> "LKR"
//    this.LRD -> "LRD"
//    this.LSL -> "LSL"
//    this.LYD -> "LYD"
//    this.MAD -> "MAD"
//    this.MDL -> "MDL"
//    this.MGA -> "MGA"
//    this.MKD -> "MKD"
//    this.MMK -> "MMK"
//    this.MNT -> "MNT"
//    this.MOP -> "MOP"
//    this.MUR -> "MUR"
//    this.MVR -> "MVR"
//    this.MWK -> "MWK"
//    this.MXN -> "MXN"
//    this.MYR -> "MYR"
//    this.MZN -> "MZN"
//    this.NAD -> "NAD"
//    this.NGN -> "NGN"
//    this.NIO -> "NIO"
//    this.NOK -> "NOK"
//    this.NPR -> "NPR"
//    this.NZD -> "NZD"
//    this.OMR -> "OMR"
//    this.PAB -> "PAB"
//    this.PEN -> "PEN"
//    this.PGK -> "PGK"
//    this.PHP -> "PHP"
//    this.PKR -> "PKR"
//    this.PLN -> "PLN"
//    this.PYG -> "PYG"
//    this.QAR -> "QAR"
//    this.RON -> "RON"
//    this.RSD -> "RSD"
//    this.RUB -> "RUB"
//    this.RWF -> "RWF"
//    this.SAR -> "SAR"
//    this.SBD -> "SBD"
//    this.SCR -> "SCR"
//    this.SDG -> "SDG"
//    this.SEK -> "SEK"
//    this.SGD -> "SGD"
//    this.SHP -> "SHP"
//    this.SLL -> "SLL"
//    this.SOS -> "SOS"
//    this.SRD -> "SRD"
//    this.SYP -> "SYP"
//    this.SZL -> "SZL"
//    this.THB -> "THB"
//    this.TJS -> "TJS"
//    this.TMT -> "TMT"
//    this.TND -> "TND"
//    this.TOP -> "TOP"
//    this.TRY -> "TRY"
//    this.TTD -> "TTD"
//    this.TWD -> "TWD"
//    this.TZS -> "TZS"
//    this.UAH -> "UAH"
//    this.UGX -> "UGX"
//    this.USD -> "USD"
//    this.UYU -> "UYU"
//    this.UZS -> "UZS"
//    this.VES -> "VES"
//    this.VND -> "VND"
//    this.VUV -> "VUV"
//    this.WST -> "WST"
//    this.XAF -> "XAF"
//    this.XCD -> "XCD"
//    this.XOF -> "XOF"
//    this.XPF -> "XPF"
//    this.YER -> "YER"
//    this.ZAR -> "ZAR"
//    this.ZMW -> "ZMW"
//    this.ZWL -> "ZWL"
//    else -> null
//}





