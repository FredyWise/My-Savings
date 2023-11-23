package com.fredy.mysavings.ViewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.RoomDatabase.Entity.UserData
import com.fredy.mysavings.Repository.AuthRepository
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.ViewModels.Event.SignInEvent
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val repository: AuthRepository
): ViewModel() {

    val _signInState = Channel<AuthState>()
    val signInState = _signInState.receiveAsFlow()

    val _googleState = mutableStateOf(
        GoogleSignInState()
    )
    val googleState: State<GoogleSignInState> = _googleState

    fun onEvent(event: SignInEvent) {
        when (event) {
            is SignInEvent.googleSignIn -> {
                viewModelScope.launch {
                    repository.googleSignIn(event.credential).collect { result ->
                        when (result) {
                            is Resource.Success -> {
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
                                _signInState.send(
                                    AuthState(
                                        isSuccess = "Sign In Success "
                                    )
                                )
                            }

                            is Resource.Loading -> {
                                _signInState.send(
                                    AuthState(
                                        isLoading = true
                                    )
                                )
                            }

                            is Resource.Error -> {
                                _signInState.send(
                                    AuthState(
                                        isError = result.message
                                    )
                                )
                            }
                        }
                    }
                }
            }

            SignInEvent.getSignedInUser -> {
                viewModelScope.launch {
                    _signInState.send(
                        AuthState(
                            signedInUser = repository.getSignedInUser()
                        )
                    )
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