package com.fredy.mysavings.Feature.Data.Util

import com.fredy.mysavings.Feature.Data.APIs.ApiCredentials
import com.google.firebase.Timestamp

fun isCacheValid(timestamp: Timestamp): Boolean {
    val timestampInMilliseconds = timestamp.seconds * 1000
    val expirationTime =
        timestampInMilliseconds + ApiCredentials.CurrencyModels.CACHE_EXPIRATION_DAYS * 24 * 60 * 60 * 1000
    return expirationTime >= System.currentTimeMillis()
}