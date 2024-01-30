package com.fredy.mysavings.Util

import kotlinx.coroutines.delay
import java.util.Timer

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}

data class ResourceState(// erase with currency
    val success: String? = null,
    val error: String? = null,
    var isLoading: Boolean = false,
){
    suspend fun setLoadingWithTimeout(timeout: Long = 3000L) {
        isLoading = true
        if (isLoading) {
            delay(timeout)
            isLoading = false
        }
    }
}