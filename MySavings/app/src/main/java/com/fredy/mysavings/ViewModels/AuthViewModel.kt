package com.fredy.mysavings.ViewModels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.Database.Entity.UserData
import com.fredy.mysavings.Data.Enum.AuthMethod
import com.fredy.mysavings.Data.Repository.AuthRepository
import com.fredy.mysavings.Data.Repository.UserRepository
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.TAG
import com.fredy.mysavings.ViewModels.Event.AuthEvent
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.storage.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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

    init {
        Log.e(TAG, "user: " + userData)
        _state.update {
            AuthState(
                signedInUser = userData
            )
        }
    }

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.GoogleAuth -> {
                viewModelScope.launch {
                    repository.googleSignIn(event.credential).collect { result ->
                        if (result is Resource.Success) {
                            upsertUser(result)
                        }
                        _state.update {
                            it.copy(
                                authResource = result,
                                authType = AuthMethod.Google
                            )
                        }
                    }
                }
            }

            is AuthEvent.SendOtp -> {
                viewModelScope.launch {
                    repository.sendOtp(
                        event.context,
                        event.phoneNumber
                    ).collect { result ->
                        if (result is Resource.Success) {
                            event.onCodeSent()
                            _state.update {
                                it.copy(
                                    sendOtpResource = result,
                                    storedVerificationId = result.data!!,
                                    authType = AuthMethod.None
                                )
                            }
                        }else{
                            _state.update {
                                it.copy(
                                    sendOtpResource = result,
                                    authType = AuthMethod.SendOTP
                                )
                            }
                        }
                    }
                }
            }

            is AuthEvent.VerifyPhoneNumber -> {
                viewModelScope.launch {
                    repository.verifyPhoneNumber(
                        event.context,
                        _state.value.storedVerificationId,
                        event.code
                    ).collect { result ->
                        if (result is Resource.Success) {
                            upsertUser(result)
                        }
                        _state.update {
                            it.copy(
                                authResource = result,
                                authType = AuthMethod.PhoneOTP
                            )
                        }
                    }
                }
            }

            is AuthEvent.LoginUser -> {
                viewModelScope.launch {
                    repository.loginUser(
                        event.email,
                        event.password
                    ).collect { result ->
                        _state.update {
                            it.copy(
                                authResource = result,
                                authType = AuthMethod.Email
                            )
                        }
                    }
                }
            }

            is AuthEvent.RegisterUser -> {
                viewModelScope.launch {
                    repository.registerUser(
                        event.email,
                        event.password
                    ).collect { result ->
                        if (result is Resource.Success) {
                            upsertUser(
                                result,
                                event.photoUrl
                            )
                        }
                        _state.update {
                            it.copy(
                                authResource = result,
                                authType = AuthMethod.Email
                            )
                        }
                    }
                }
            }

            AuthEvent.GetCurrentUser -> {
                viewModelScope.launch {
                    repository.getCurrentUser()?.let { currentUser ->
                        Log.d(TAG, "currentUser: "+currentUser)
                        _state.update {
                            it.copy(
                                signedInUser = currentUser
                            )
                        }
                    }
                }
            }

            AuthEvent.SignOut -> {
                _state.update {
                    AuthState()
                }
                viewModelScope.launch {
                    repository.signOut()
                }
            }
        }
    }

    private suspend fun upsertUser(
        result: Resource<AuthResult>,
        photoUri: Uri? = null
    ) {
        val user = result.data!!.user
        val userData = user?.run {
            val profilePictureUrl = photoUri?.let {
                uploadProfilePicture(
                    uid, it
                )
            } ?: run {
                photoUrl.toString()
            }
            UserData(
                firebaseUserId = uid,
                username = displayName,
                emailOrPhone = email?:phoneNumber,
                profilePictureUrl = profilePictureUrl
            )
        }
        userRepository.upsertUser(
            userData!!
        )
    }

    private suspend fun uploadProfilePicture(
        uid: String, imageUri: Uri
    ): String {
        val storageRef = Firebase.storage.reference
        val profilePictureRef = storageRef.child("profile_pictures/$uid.jpg")

        return try {
            val downloadUri = profilePictureRef.putFile(
                imageUri
            ).await().storage.downloadUrl.await()
            downloadUri.toString()
        } catch (e: Exception) {
            throw e
        }
    }
}

data class AuthState(
    val authResource: Resource<AuthResult> = Resource.Loading(),
    val sendOtpResource: Resource<String> = Resource.Loading(),
    val authType: AuthMethod = AuthMethod.None,
    val signedInUser: UserData? = null,
    var storedVerificationId: String = ""
)



