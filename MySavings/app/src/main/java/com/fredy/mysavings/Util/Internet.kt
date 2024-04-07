package com.fredy.mysavings.Util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.fredy.mysavings.Feature.Data.APIs.ApiCredentials
import com.google.firebase.Timestamp

fun isInternetConnected(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCapabilities = connectivityManager.activeNetwork
        ?: return false
    val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities)
        ?: return false
    val result = when {
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
//        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
    return result
}

fun isCacheValid(timestamp: Timestamp): Boolean {
    val timestampInMilliseconds = timestamp.seconds * 1000
    val expirationTime =
        timestampInMilliseconds + ApiCredentials.CurrencyModels.CACHE_EXPIRATION_DAYS * 24 * 60 * 60 * 1000
    return expirationTime >= System.currentTimeMillis()
}