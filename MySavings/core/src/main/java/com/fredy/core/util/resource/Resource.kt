package com.fredy.core.util.resource


sealed interface Resource<out D, out E: ResourceError> {
    data class Success<out D, out E: ResourceError>(val data: D): Resource<D, E>
    data class Error<out D, out E: ResourceError>(val error: E): Resource<D, E>
    class Loading<out D, out E: ResourceError>(): Resource<D, E>
}
