package com.fredy.data.database.dto

import com.google.firebase.Timestamp

data class FirebaseRatesCache(
    val cacheId: String = "",
    val base: String = "",
    val date: String = "",
    val rates: String = "",
    val success: Boolean = false,
    val timestamp: Int = 0,
    val cachedTime: Timestamp = Timestamp.now()
)

