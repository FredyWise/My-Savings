package com.fredy.mysavings.Feature.Presentation.ViewModels.AuthViewModel

import com.fredy.domain.enums.AuthMethod
import com.fredy.domain.model.UserData
import com.google.firebase.auth.AuthResult

data class AuthState(
    val updateResource: Resource<String> = Resource.Success(""),
    val bioAuthResource: Resource<String> = Resource.Loading(),
    val authResource: Resource<AuthResult> = Resource.Loading(),
    val sendOtpResource: Resource<String> = Resource.Loading(),
    val authType: AuthMethod = AuthMethod.None,
    val signedInUser: UserData? = null,
    val isSignedIn: Boolean = false,
)