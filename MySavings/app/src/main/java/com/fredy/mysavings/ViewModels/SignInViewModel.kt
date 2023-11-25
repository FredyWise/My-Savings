package com.fredy.mysavings.ViewModels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.RoomDatabase.Entity.UserData
import com.fredy.mysavings.Repository.AuthRepository
import com.fredy.mysavings.Repository.UserRepository
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.ViewModels.Event.SignInEvent
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
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

    fun onEvent(event: SignInEvent) {
        when (event) {
            is SignInEvent.googleSignIn -> {
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

            is SignInEvent.loginUser -> {
                viewModelScope.launch {
                    repository.loginUser(
                        event.email,
                        event.password
                    ).collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                var userData: UserData?
                                result.data?.user?.let {
                                    userData = userRepository.getUser(
                                        it.uid
                                    ).firstOrNull()
                                    if (userData != null) {
                                        _state.update {
                                            AuthState(
                                                isSuccess = "Sign In Success"
                                            )
                                        }
                                    } else {
                                        _state.update {
                                            AuthState(
                                                isError = "User Not Found!!!"
                                            )
                                        }
                                    }
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

            SignInEvent.getSignedInUser -> {
                viewModelScope.launch {
                    _state.update {
                        AuthState(
                            signedInUser = repository.getSignedInUser()
                        )
                    }
                }
            }

            SignInEvent.signOut -> {
                viewModelScope.launch {
                    repository.signOut()
                }
            }
        }
    }
}

data class GoogleSignInState(
    val success: AuthResult? = null,
    val loading: Boolean = false,
    val error: String = "",
    val signInClient: GoogleSignInClient? = null
)