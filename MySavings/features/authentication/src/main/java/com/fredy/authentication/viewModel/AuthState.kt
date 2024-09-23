package com.fredy.authentication.viewModel

import com.fredy.authentication.domain.AuthMethod
import com.fredy.domain.model.UserData
import com.fredy.domain.util.resource.DataError
import com.fredy.domain.util.resource.Resource


data class AuthState(
    val updateResource: Resource<String, DataError.Authentication> = Resource.Success(
        ""
    ),
    val authResource: Resource<String, DataError.Authentication> = Resource.Loading(),
    val sendOtpResource: Resource<String, DataError.Authentication> = Resource.Loading(),
    val authType: AuthMethod = AuthMethod.None,
    val signedInUser: UserData? = null,
    val isSignedIn: Boolean = false,
)