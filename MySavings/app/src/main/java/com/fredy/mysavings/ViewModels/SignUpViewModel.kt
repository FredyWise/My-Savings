package com.fredy.mysavings.ViewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.RoomDatabase.Entity.UserData
import com.fredy.mysavings.Repository.AuthRepository
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.ViewModels.Event.SignUpEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: AuthRepository
): ViewModel() {
    private val _state = MutableStateFlow(
        AuthState()
    )
    val state = _state.asStateFlow()

    val _googleState = mutableStateOf(
        GoogleSignInState()
    )
    val googleState: State<GoogleSignInState> = _googleState

    fun onEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.googleSignIn -> {
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

            is SignUpEvent.registerUser -> {
                viewModelScope.launch {
                    repository.registerUser(
                        event.email,
                        event.password
                    ).collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                _state.update {
                                    AuthState(
                                        isSuccess = "Sign Up Success "
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
        }
    }
}


data class AuthState(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String? = "",
    val signedInUser: UserData? = null
)