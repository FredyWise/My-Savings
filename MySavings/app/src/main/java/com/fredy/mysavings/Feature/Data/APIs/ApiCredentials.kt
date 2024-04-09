package com.fredy.mysavings.Feature.Data.APIs

sealed class ApiCredentials{
    object CurrencyModels{
        const val BASE_URL = "https://api.apilayer.com/"
        const val API_KEY = "UBRTLRTJyilUycEIKmgt2AdgymP45YmU"
        const val GET_LATEST = "/exchangerates_data/latest"
        const val CACHE_EXPIRATION_DAYS = 10
        const val BASE_CURRENCY = "USD"
    }
    object TextCorrectionModels{
        const val BASE_URL = "https://api.typewise.ai/"
        const val POST_LATEST_CORRECTION = "/latest/correction/whole_sentence"
    }
    object CountryModels{
        const val BASE_URL = "https://restcountries.com/"
        const val GET_ALL_COUNTRY = "/v3.1/all"
        const val GET_ALL_CURRENCY_INFO = "flags,currencies"
        const val CURRENCY_INFO_ID = "currencyInfo"
    }
}