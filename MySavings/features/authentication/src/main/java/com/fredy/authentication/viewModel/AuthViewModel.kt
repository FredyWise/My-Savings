package com.fredy.authentication.viewModel

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.authentication.domain.AuthMethod
import com.fredy.authentication.domain.usecases.AuthUseCases
import com.fredy.domain.model.UserData
import com.fredy.domain.useCases.UserUseCases.UserUseCases
import com.fredy.domain.util.resource.DataError
import com.fredy.domain.util.resource.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val authUseCases: AuthUseCases,
    private val userUseCases: UserUseCases,
    private val currentUserData: UserData?,
) : ViewModel() {
    private val bioAuthStatus = MutableStateFlow(
        false
    )

    private val storedVerificationId = MutableStateFlow(
        ""
    )

    private val _state = MutableStateFlow(
        AuthState()
    )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update {
                it.copy(signedInUser = currentUserData)
            }
            onEvent(AuthEvent.GetCurrentUser)
        }
        viewModelScope.launch {
            savedStateHandle.get<Boolean>("bioAuthStatus")?.let { stat ->
                bioAuthStatus.update { stat }
            }
        }
    }

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.GoogleAuth -> {
                viewModelScope.launch {
                    authUseCases.googleSignIn(
                        event.credential
                    ).collect { result ->
                        _state.update {
                            it.copy(
                                authResource = when (result) {
                                    is Resource.Success -> {
                                        userUseCases.insertUser(result.data.user)
                                        Resource.Success("Google Auth Success")
                                    }

                                    is Resource.Loading -> Resource.Loading()
                                    is Resource.Error -> Resource.Error(result.error)
                                },
                                isSignedIn = result is Resource.Success,
                                authType = AuthMethod.Google
                            )
                        }
                    }
                }
            }

            is AuthEvent.LoginUser -> {
                viewModelScope.launch {
                    authUseCases.emailSignIn(
                        event.email,
                        event.password
                    ).collect { result ->
                        _state.update {
                            it.copy(
                                authResource = when (result) {
                                    is Resource.Success -> {
                                        userUseCases.insertUser(result.data.user)
                                        Resource.Success("Email Auth Success")
                                    }

                                    is Resource.Loading -> Resource.Loading()
                                    is Resource.Error -> Resource.Error(result.error)
                                },
                                isSignedIn = result is Resource.Success,
                                authType = AuthMethod.Email
                            )
                        }
                    }
                }
            }

            is AuthEvent.RegisterUser -> {
                viewModelScope.launch {
                    authUseCases.registerUser(
                        event.email,
                        event.password
                    ).collect { result ->
                        _state.update {
                            it.copy(
                                authResource = when (result) {
                                    is Resource.Success -> {
                                        val user = result.data.user
                                        userUseCases.insertUser(user)
                                        user?.let {
                                            userUseCases.uploadProfilePicture(user.uid,event.photoUrl)
                                        }
                                        Resource.Success("Registration Success")
                                    }

                                    is Resource.Loading -> Resource.Loading()
                                    is Resource.Error -> Resource.Error(result.error)
                                },
                                isSignedIn = result is Resource.Success,
                                authType = AuthMethod.Email
                            )
                        }
                    }
                }
            }

            is AuthEvent.SendOtp -> {
                viewModelScope.launch {
                    authUseCases.sendOtp(
                        event.context,
                        event.phoneNumber
                    ).collect { result ->
                        _state.update {
                            it.copy(
                                sendOtpResource = result,
                                authType = when (result) {
                                    is Resource.Loading -> AuthMethod.SendOTP
                                    is Resource.Success -> {
                                        event.onCodeSent()
                                        storedVerificationId.update { result.data }
                                        AuthMethod.None
                                    }
                                    else -> it.authType
                                }
                            )
                        }

                    }
                }
            }

            is AuthEvent.VerifyPhoneNumber -> {
                viewModelScope.launch {
                    authUseCases.verifyPhoneNumber(
//                        event.context,
                        storedVerificationId.value,
                        event.code
                    ).collect { result ->
                        _state.update {
                            it.copy(
                                authResource = when (result) {
                                    is Resource.Success -> {
                                        val user = result.data.user
                                        userUseCases.insertUser(user)
                                        Resource.Success("Phone Number Auth Success")
                                    }

                                    is Resource.Loading -> Resource.Loading()
                                    is Resource.Error -> Resource.Error(result.error)
                                },
                                isSignedIn = result is Resource.Success,
                                authType = AuthMethod.PhoneOTP
                            )
                        }
                    }
                }
            }

            is AuthEvent.UpdateUserData -> {
                viewModelScope.launch {
                    _state.value.signedInUser!!.run {
                        val profilePictureUrl = if (event.photoUrl != Uri.EMPTY) {
                            userUseCases.uploadProfilePicture(
                                firebaseUserId,
                                event.photoUrl
                            )
                        } else {
                            profilePictureUrl
                        }
                        authUseCases.updateUserInformation(
                            profilePictureUrl?.toUri(),
                            event.username,
                            event.oldPassword,
                            event.password
                        ).collect { updateResource ->
                            if (updateResource is Resource.Success) {
                                val user = UserData(
                                    firebaseUserId = firebaseUserId,
                                    username = event.username,
                                    email = email,
                                    phone = phone,
                                    profilePictureUrl = profilePictureUrl
                                )
                                userUseCases.updateUser(
                                    user
                                )
                            }

                            _state.update {
                                it.copy(
                                    updateResource = updateResource
                                )
                            }
                        }
                    }
                    onEvent(AuthEvent.GetCurrentUser)
                }
            }

            AuthEvent.GetCurrentUser -> {
                viewModelScope.launch {
                    userUseCases.getCurrentUser().collectLatest { currentUser ->
                        if (currentUser is Resource.Success) {
                            currentUser.data?.let { user ->
                                _state.update {
                                    it.copy(
                                        signedInUser = user,
                                        isSignedIn = true,
                                        updateResource = Resource.Success(
                                            ""
                                        ),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            is AuthEvent.SignOut -> {
                _state.update {
                    AuthState()
                }
                viewModelScope.launch {
                    authUseCases.signOut()
                }
            }

            AuthEvent.BioAuth -> {
                viewModelScope.launch {
                    if (bioAuthStatus.value && currentUserData != null && state.value.isSignedIn) {
                        authUseCases.bioAuth(
                            context
                        ).collectLatest { authResource ->
                            _state.update {
                                it.copy(
                                    authResource = authResource
                                )
                            }
                        }
                    } else {
                        _state.update {
                            it.copy(
                                authResource = Resource.Error(
                                    DataError.Authentication.INVALID_CREDENTIALS
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}