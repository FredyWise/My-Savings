package com.fredy.mysavings.Feature.Domain.Util

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}

//sealed interface Resource<out D, out E: ResourceError> {
//    data class Success<out D, out E: ResourceError>(val data: D): Resource<D, E>
//    data class Error<out D, out E: ResourceError>(val error: E): Resource<D, E>
//    class Loading<out D, out E: ResourceError>: Resource<D, E>
//}
