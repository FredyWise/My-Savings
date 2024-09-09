package com.fredy.domain.util.resource


sealed interface DataError: ResourceError {
    enum class Authentication: DataError {
        INVALID_CREDENTIALS,
        NO_INTERNET,
        REQUEST_TIMEOUT,
        REGISTRATION_FAILED,
        USER_NOT_FOUND,
        UPDATE_PASSWORD_FAILED,
        UPDATE_DISPLAY_NAME_FAILED,
        REAUTHENTICATION_FAILED,
        USER_CANCELLED,
        UNKNOWN
    }

    enum class Database: DataError {
        NO_INTERNET,
        REQUEST_TIMEOUT,
        UNKNOWN,
    }


    enum class Network: DataError {
        REQUEST_TIMEOUT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        PAYLOAD_TOO_LARGE,
        SERVER_ERROR,
        SERIALIZATION,
        UNKNOWN
    }
    enum class Local: DataError {
        DISK_FULL
    }
}