package com.fredy.mysavings.ViewModels

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.Database.Entity.UserData
import com.fredy.mysavings.Repository.AuthRepository
import com.fredy.mysavings.Repository.UserRepository
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.TAG
import com.fredy.mysavings.ViewModels.Event.AuthEvent
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.storage.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val userData: UserData?,
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
        _state.update {
            AuthState(
                signedInUser = userData
            )
        }
        Log.e(TAG, "user: "+userData, )
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
                                    val profilePictureUrl = uploadProfilePicture(uid,event.photoUrl)
                                    UserData(
                                        firebaseUserId = uid,
                                        username = displayName ?: event.username,
                                        email = email ?: event.email,
                                        profilePictureUrl = profilePictureUrl
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

            AuthEvent.getCurrentUser -> {
                viewModelScope.launch {
                    repository.getCurrentUser()?.let { currentUser ->
                        _state.update { it.copy(signedInUser = currentUser) }
                    }
                }
            }

            AuthEvent.signOut -> {
                _state.update {
                    AuthState()
                }
                viewModelScope.launch {
                    repository.signOut()
                }
            }
        }
    }
    private suspend fun uploadProfilePicture(uid: String, imageUri: Uri): String {
        val storageRef = Firebase.storage.reference
        val profilePictureRef = storageRef.child("profile_pictures/$uid.jpg")

        return try {
            val downloadUri = profilePictureRef.putFile(imageUri).await().storage.downloadUrl.await()
            downloadUri.toString()
        } catch (e: Exception) {
            throw e
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

