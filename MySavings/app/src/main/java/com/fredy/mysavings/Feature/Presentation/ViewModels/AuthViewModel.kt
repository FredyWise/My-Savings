package com.fredy.mysavings.Feature.Presentation.ViewModels

import android.content.Context
import android.hardware.biometrics.BiometricPrompt
import android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
import android.net.Uri
import android.os.CancellationSignal
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Domain.Model.UserData
import com.fredy.mysavings.Feature.Data.Enum.AuthMethod
import com.fredy.mysavings.Feature.Domain.Repository.AuthRepository
import com.fredy.mysavings.Feature.Domain.Repository.SettingsRepository
import com.fredy.mysavings.Feature.Domain.UseCases.UserUseCases.UserUseCases
import com.fredy.mysavings.Util.Log
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.fredy.mysavings.Feature.Presentation.ViewModels.Event.AuthEvent
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.storage.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authUseCases: AuthRepository,
    private val settingsRepository: SettingsRepository,
    private val userUseCases: UserUseCases,
    private val currentUserData: UserData?,
) : ViewModel() {

    private val storedVerificationId = MutableStateFlow("")

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
    }

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.GoogleAuth -> {
                viewModelScope.launch {
                    authUseCases.googleSignIn(event.credential).collect { result ->
                        if (result is Resource.Success) {
                            insertUser(result)
                            _state.update {
                                it.copy(isSignedIn = true)
                            }
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
                    authUseCases.sendOtp(
                        event.context,
                        event.phoneNumber
                    ).collect { result ->
                        when (result) {
                            is Resource.Error -> {
                                _state.update {
                                    it.copy(
                                        sendOtpResource = result,
                                    )
                                }
                            }

                            is Resource.Loading -> {
                                _state.update {
                                    it.copy(
                                        sendOtpResource = result,
                                        authType = AuthMethod.SendOTP
                                    )
                                }
                            }

                            is Resource.Success -> {
                                event.onCodeSent()
                                storedVerificationId.update { result.data!! }
                                _state.update {
                                    it.copy(
                                        sendOtpResource = result,
                                        authType = AuthMethod.None
                                    )
                                }
                            }
                        }
                    }
                }
            }

            is AuthEvent.VerifyPhoneNumber -> {
                viewModelScope.launch {
                    authUseCases.verifyPhoneNumber(
                        event.context,
                        storedVerificationId.value,
                        event.code
                    ).collect { result ->
                        if (result is Resource.Success) {
                            insertUser(result)
                            _state.update {
                                it.copy(isSignedIn = true)
                            }
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
                    authUseCases.loginUser(
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
                    authUseCases.registerUser(
                        event.email,
                        event.password
                    ).collect { result ->
                        if (result is Resource.Success) {
                            insertUser(
                                result,
                                event.photoUrl
                            )
                            _state.update {
                                it.copy(isSignedIn = true)
                            }
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
                    _state.value.signedInUser!!.run {
                        val profilePictureUrl = if (event.photoUrl != Uri.EMPTY) {
                            uploadProfilePicture(
                                firebaseUserId, event.photoUrl
                            )
                        } else {
                            profilePictureUrl
                        }
                        authUseCases.updateUserInformation(
                            profilePictureUrl?.toUri(),
                            event.username,
                            event.oldPassword,
                            event.password
                        ).collectLatest { updateResource ->
                            when (updateResource) {
                                is Resource.Success -> {
                                    val user = UserData(
                                        firebaseUserId = firebaseUserId,
                                        username = event.username,
                                        email = email,
                                        phone = phone,
                                        userCurrency = userCurrency,
                                        profilePictureUrl = profilePictureUrl
                                    )
                                    userUseCases.updateUser(user)
                                }

                                else -> {
                                }
                            }
                            _state.update { it.copy(updateResource = updateResource) }
                        }
                    }
                    onEvent(AuthEvent.GetCurrentUser)
                }
            }

            AuthEvent.GetCurrentUser -> {
                viewModelScope.launch {
                    userUseCases.getCurrentUser().collectLatest { currentUser ->
                        when (currentUser) {
                            is Resource.Success -> {
                                currentUser.data?.let { user ->
                                    _state.update {
                                        it.copy(
                                            signedInUser = user,
                                            isSignedIn = true
                                        )
                                    }
                                }
                            }

                            else -> {
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
                if (settingsRepository.bioAuthStatus() && currentUserData.isNotNull() && state.value.isSignedIn) {
                    val title = "Login"
                    val subtitle = "Login into your account"
                    val description =
                        "Put your finger on the fingerprint sensor or scan your face to authorise your account."
                    val negativeText = "Cancel"
                    val executor = ContextCompat.getMainExecutor(
                        context
                    )
                    val biometricPrompt = BiometricPrompt.Builder(context)
                        .apply {
                            setTitle(title)
                            setSubtitle(subtitle)
                            setDescription(description)
                            setConfirmationRequired(false)
                            setNegativeButton(negativeText, executor) { _, _ ->
                                _state.update {
                                    it.copy(bioAuthResource = Resource.Error("Bio Auth Canceled"))
                                }
                            }
                        }.build()

                    biometricPrompt.authenticate(
                        CancellationSignal(), executor,
                        object : AuthenticationCallback() {
                            override fun onAuthenticationError(
                                errorCode: Int,
                                errString: CharSequence
                            ) {
                                super.onAuthenticationError(
                                    errorCode, errString
                                )
                                Log.e(
                                    "onAuthenticationError:\n error code:$errorCode\n $errString",
                                )
                                _state.update {
                                    it.copy(bioAuthResource = Resource.Error("$errString"))
                                }
                            }

                            override fun onAuthenticationSucceeded(
                                result: BiometricPrompt.AuthenticationResult
                            ) {
                                super.onAuthenticationSucceeded(
                                    result
                                )
                                _state.update {
                                    it.copy(bioAuthResource = Resource.Success("Bio Auth Success"))
                                }
                            }

                            override fun onAuthenticationFailed() {
                                super.onAuthenticationFailed()
                                _state.update {
                                    it.copy(bioAuthResource = Resource.Error("Bio Auth Failed"))
                                }
                            }
                        },
                    )
                } else {
                    _state.update {
                        it.copy(bioAuthResource = Resource.Error("Your account are not saved please login in other way"))
                    }
                }
            }
        }
    }

    private suspend fun insertUser(
        result: Resource<AuthResult>,
        photoUri: Uri = Uri.EMPTY
    ) {
        val user = result.data!!.user
        user?.run {
            val profilePictureUrl = if (photoUri != Uri.EMPTY) {
                uploadProfilePicture(
                    uid, photoUri
                )
            } else {
                photoUrl.toString()
            }
            userUseCases.insertUser(
                UserData(
                    firebaseUserId = uid,
                    username = displayName,
                    email = email,
                    phone = phoneNumber,
                    profilePictureUrl = profilePictureUrl
                )
            )
        }

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
    val updateResource: Resource<String> = Resource.Success(""),
    val bioAuthResource: Resource<String> = Resource.Loading(),
    val authResource: Resource<AuthResult> = Resource.Loading(),
    val sendOtpResource: Resource<String> = Resource.Loading(),
    val authType: AuthMethod = AuthMethod.None,
    val signedInUser: UserData? = null,
    val isSignedIn: Boolean = false,

    )



