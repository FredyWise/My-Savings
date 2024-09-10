package com.fredy.domain.model


import com.google.firebase.Timestamp


data class RatesCache(
    val cacheId: String = "",
    val base: String = "",
    val date: String = "",
    val rates: List<Rate> = emptyList(),
    val success: Boolean = false,
    val timestamp: Int = 0,
    val cachedTime: Timestamp = Timestamp.now()
)

