package com.fredy.mysavings.Feature.Data.Database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fredy.mysavings.Feature.Data.APIs.CurrencyModels.Response.CurrencyResponse
import com.fredy.mysavings.Feature.Data.Database.Converter.CurrencyRatesConverter
import com.google.firebase.Timestamp

@Entity
data class RatesCache(
    @PrimaryKey
    val base: String = "",
    val date: String = "",
    val rates: String = "",
    val success: Boolean = false,
    val timestamp: Int = 0,
    val cachedTime: Timestamp = Timestamp.now()
) {

    constructor(
        currencyResponse: CurrencyResponse,
        cachedTime: Timestamp
    ): this(
        currencyResponse.base,
        currencyResponse.date,
        CurrencyRatesConverter.fromRates(currencyResponse.rates),
        currencyResponse.success,
        currencyResponse.timestamp,
        cachedTime
    )
    constructor() : this("", "", "", false, 0, Timestamp.now())
}



