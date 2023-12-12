package com.fredy.mysavings.Data.APIs

sealed class ApiCredentials{
    object CurrencyModels{
        const val BASE_URL = "https://api.apilayer.com/"
        const val API_KEY = "ENXX0mTPZmDfoYwXTFQff6UN8ruMJTOH"
        const val GET_LATEST = "/exchangerates_data/latest"
    }
    object TextCorrectionModule{
        const val BASE_URL = "https://api.typewise.ai"
        const val POST_LATEST_CORRECTION = "/latest/correction/whole_sentence"
    }
}