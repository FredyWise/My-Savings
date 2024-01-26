package com.fredy.mysavings.Data.Database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fredy.mysavings.Data.APIs.CurrencyModels.Response.CurrencyResponse
import com.fredy.mysavings.Data.APIs.CurrencyModels.Response.Rates
import com.fredy.mysavings.Data.Database.Converter.TimestampConverter
import com.google.firebase.Timestamp
import java.time.LocalDateTime

@Entity
data class CurrencyCache(
    @PrimaryKey
    val cacheId: String = "",
    val base: String,
    val date: String,
    val rates: Rates,
    val success: Boolean,
    val timestamp: Int,
    val cachedTime: Timestamp
){
    val currencyResponse: CurrencyResponse
        get() = CurrencyResponse(
            base, date, rates, success, timestamp
        )

    constructor(
        currencyResponse: CurrencyResponse,
        cachedTime: Timestamp
    ): this(
        "",
        currencyResponse.base,
        currencyResponse.date,
        currencyResponse.rates,
        currencyResponse.success,
        currencyResponse.timestamp,
        cachedTime
    )
}