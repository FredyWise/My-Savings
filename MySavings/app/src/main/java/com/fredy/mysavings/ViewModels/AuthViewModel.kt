package com.fredy.mysavings.ViewModels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.Database.Model.UserData
import com.fredy.mysavings.Data.Enum.AuthMethod
import com.fredy.mysavings.Data.Repository.AuthRepository
import com.fredy.mysavings.Data.Repository.UserRepository
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.ViewModels.Event.AuthEvent
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.storage.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val userRepository: UserRepository,
    private val currentUserData: UserData?,
) : ViewModel() {

    private val _state = MutableStateFlow(
        AuthState()
    )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update {
                AuthState(
                    signedInUser = currentUserData
                )
            }
        }
    }

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.GoogleAuth -> {
                viewModelScope.launch {
                    repository.googleSignIn(event.credential).collect { result ->
                        if (result is Resource.Success) {
                            insertUser(result)
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
                        } else {
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
                            insertUser(result)
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
                            insertUser(
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

            is AuthEvent.UpdateUserData -> {
                viewModelScope.launch {
                    _state.update { it.copy(updateResource = Resource.Loading()) }
                    val user = _state.value.signedInUser!!.run {
                        val profilePictureUrl = if (event.photoUrl != Uri.EMPTY) {
                            uploadProfilePicture(
                                firebaseUserId, event.photoUrl
                            )
                        } else {
                            profilePictureUrl
                        }
                        UserData(
                            firebaseUserId = firebaseUserId,
                            username = event.username,
                            emailOrPhone = event.email,
                            profilePictureUrl = profilePictureUrl
                        )
                    }
                    userRepository.upsertUser(user)
                    _state.update { it.copy(updateResource = Resource.Success("User Data Successfully Updated")) }
                    onEvent(AuthEvent.GetCurrentUser)

                }
            }

            AuthEvent.GetCurrentUser -> {
                viewModelScope.launch {
                    userRepository.getCurrentUser().collectLatest { currentUser ->
                        when (currentUser) {
                            is Resource.Success -> {
                                currentUser.data?.let { user ->
                                    _state.update {
                                        it.copy(
                                            signedInUser = user
                                        )
                                    }
                                }
                            }
                            else -> {
                            }
                        }
                    }
                    _state.update { it.copy(updateResource = Resource.Error("")) }
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

    private suspend fun insertUser(
        result: Resource<AuthResult>,
        photoUri: Uri = Uri.EMPTY
    ) {
        val user = result.data!!.user
        val userData = user?.run {
            val profilePictureUrl = if (photoUri != Uri.EMPTY) {
                uploadProfilePicture(
                    uid, photoUri
                )
            } else {
                photoUrl.toString()
            }
            UserData(
                firebaseUserId = uid,
                username = displayName,
                emailOrPhone = email ?: phoneNumber,
                profilePictureUrl = profilePictureUrl
            )
        }
        userRepository.insertUser(
            userData!!
        )
    }

    private suspend fun uploadProfilePicture(
        uid: String, imageUri: Uri
    ): String {
        return try {
            val storageRef = Firebase.storage.reference
            val profilePictureRef = storageRef.child("profile_pictures/$uid.jpg")
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
    val updateResource: Resource<String> = Resource.Error(""),
    val authResource: Resource<AuthResult> = Resource.Loading(),
    val sendOtpResource: Resource<String> = Resource.Loading(),
    val authType: AuthMethod = AuthMethod.None,
    val signedInUser: UserData? = null,
    var storedVerificationId: String = ""
)



