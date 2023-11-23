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
import com.fredy.mysavings.ViewModel.CategoryMap
import com.fredy.mysavings.ViewModel.CategoryState
import com.fredy.mysavings.ViewModels.Event.SignUpEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
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

    fun onEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.googleSignIn -> {
                viewModelScope.launch {
                    authRepository.googleSignIn(event.credential).collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                result.data?.let {
                                    it.user?.let { user ->
                                        val userData = UserData(
                                            firebaseUserId = user.uid,
                                            username = user.displayName,
                                            email = user.email,
                                            profilePictureUrl = user.photoUrl.toString()
                                        )
                                        userRepository.upsertUser(userData).wait()
                                    }
                                }
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
                    authRepository.registerUser(
                        event.email,
                        event.password
                    ).collect { result ->

                        when (result) {
                            is Resource.Success -> {
                                result.data?.let {
                                    it.user?.let { user ->
                                        val userData = UserData(
                                            firebaseUserId = user.uid,
                                            username = event.username,
                                            email = user.email,
                                            profilePictureUrl = user.photoUrl.toString()
                                        )
                                        userRepository.upsertUser(userData)
                                    }
                                }
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