package com.fredy.mysavings.ViewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.Database.Entity.UserData
import com.fredy.mysavings.Repository.AuthRepository
import com.fredy.mysavings.Repository.UserRepository
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.ViewModels.Event.AuthEvent
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val userRepository: UserRepository
): ViewModel() {

    private val _state = MutableStateFlow(
        AuthState()
    )
    val state = _state.asStateFlow()

    val _googleState = mutableStateOf(
        GoogleSignInState()
    )
    val googleState: State<GoogleSignInState> = _googleState

    init {
        viewModelScope.launch {
            _state.update {
                AuthState(
                    signedInUser = repository.getSignedInUser()
                )
            }
        }
    }

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.googleAuth -> {
                viewModelScope.launch {
                    repository.googleSignIn(event.credential).collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                val user = result.data!!.user
                                val userData = user?.run {
                                    UserData(
                                        firebaseUserId = uid,
                                        username = displayName,
                                        email = email,
                                        profilePictureUrl = photoUrl.toString()
                                    )
                                }
                                userRepository.upsertUser(
                                    userData!!
                                )
                                _googleState.value = GoogleSignInState(
                                    success = result.data
                                )

                            }

                            is Resource.Loading -> {
                                _googleState.value = GoogleSignInState(
                                    loading = true
                                )
                            }

                            is Resource.Error -> {
                                _googleState.value = GoogleSignInState(
                                    error = result.message!!
                                )
                            }
                        }
                    }
                }
            }

            is AuthEvent.loginUser -> {
                viewModelScope.launch {
                    repository.loginUser(
                        event.email,
                        event.password
                    ).collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                _state.update {
                                    AuthState(
                                        isSuccess = "Sign In Success"
                                    )
                                }
                            }

                            is Resource.Loading -> {
                                _state.update {
                                    AuthState(
                                        isLoading = true
                                    )
                                }
                            }

                            is Resource.Error -> {
                                _state.update {
                                    AuthState(
                                        isError = result.message
                                    )
                                }
                            }
                        }
                    }
                }
            }

            is AuthEvent.registerUser -> {
                viewModelScope.launch {
                    repository.registerUser(
                        event.email,
                        event.password
                    ).collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                val user = result.data!!.user
                                val userData = user?.run {
                                    UserData(
                                        firebaseUserId = uid,
                                        username = event.username,
                                        email = email,
                                        profilePictureUrl = photoUrl.toString()
                                    )
                                }
                                userRepository.upsertUser(
                                    userData!!
                                )
                                _state.update {
                                    AuthState(
                                        isSuccess = "Sign Up Success"
                                    )
                                }
                            }

                            is Resource.Loading -> {
                                _state.update {
                                    AuthState(
                                        isLoading = true
                                    )
                                }
                            }

                            is Resource.Error -> {
                                _state.update {
                                    AuthState(
                                        isError = result.message
                                    )
                                }
                            }
                        }
                    }
                }
            }

            AuthEvent.getSignedInUser -> {
                viewModelScope.launch {
                    _state.update {
                        AuthState(
                            signedInUser = repository.getSignedInUser()
                        )
                    }
                }
            }

            AuthEvent.signOut -> {
                viewModelScope.launch {
                    repository.signOut()
                }
            }
        }
    }
}

data class AuthState(
    val isLoading: Boolean = false,
    val isSuccess: String? = null,
    val isError: String? = null,
    val signedInUser: UserData? = null
)

data class GoogleSignInState(
    val success: AuthResult? = null,
    val loading: Boolean = false,
    val error: String = "",
    val signInClient: GoogleSignInClient? = null
)