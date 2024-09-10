package com.fredy.domain.util

import com.fredy.core.credentials.ApiCredentials
import com.google.firebase.Timestamp

fun isCacheValid(timestamp: Timestamp): Boolean {
    val timestampInMilliseconds = timestamp.seconds * 1000
    val expirationTime =
        timestampInMilliseconds + ApiCredentials.CurrencyModels.CACHE_EXPIRATION_DAYS * 24 * 60 * 60 * 1000
    return expirationTime >= System.currentTimeMillis()
}