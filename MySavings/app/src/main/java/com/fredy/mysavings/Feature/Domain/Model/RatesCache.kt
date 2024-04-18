package com.fredy.mysavings.Feature.Domain.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fredy.mysavings.Feature.Data.APIs.CurrencyModels.Response.Rates
import com.google.firebase.Timestamp

@Entity
data class RatesCache(
    @PrimaryKey
    val cacheId: String = "",
    val base: String = "",
    val date: String = "",
    val rates: Rates = Rates(),
    val success: Boolean = false,
    val timestamp: Int = 0,
    val cachedTime: Timestamp = Timestamp.now()
)


data class FirebaseRatesCache(
    val cacheId: String = "",
    val base: String = "",
    val date: String = "",
    val rates: String = "",
    val success: Boolean = false,
    val timestamp: Int = 0,
    val cachedTime: Timestamp = Timestamp.now()
)
