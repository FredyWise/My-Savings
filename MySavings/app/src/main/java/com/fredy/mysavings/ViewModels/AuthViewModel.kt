package com.fredy.mysavings.ViewModels

import android.content.Context
import android.hardware.biometrics.BiometricPrompt
import android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
import android.net.Uri
import android.os.CancellationSignal
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Data.Database.Model.UserData
import com.fredy.mysavings.Data.Enum.AuthMethod
import com.fredy.mysavings.Data.Repository.AuthRepository
import com.fredy.mysavings.Data.Repository.SettingsRepository
import com.fredy.mysavings.Data.Repository.UserRepository
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.TAG
import com.fredy.mysavings.ViewModels.Event.AuthEvent
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
    private val repository: AuthRepository,
    private val settingsRepository: SettingsRepository,
    private val userRepository: UserRepository,
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
                    repository.googleSignIn(event.credential).collect { result ->
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
                    repository.sendOtp(
                        event.context,
                        event.phoneNumber
                    ).collect { result ->
                        if (result is Resource.Success) {
                            event.onCodeSent()
                            storedVerificationId.update { result.data!! }
                            _state.update {
                                it.copy(
                                    sendOtpResource = result,
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
                        repository.updateUserInformation(
                            profilePictureUrl?.toUri(),
                            event.username,
                            event.email,
                            event.oldPassword,
                            event.password
                        ).collectLatest { updateResource ->
                            when (updateResource) {
                                is Resource.Success -> {
                                    val user = UserData(
                                        firebaseUserId = firebaseUserId,
                                        username = event.username,
                                        emailOrPhone = event.email,
                                        userCurrency = userCurrency,
                                        profilePictureUrl = profilePictureUrl
                                    )
                                    userRepository.upsertUser(user)
                                    _state.update { it.copy(updateResource = updateResource) }
                                }

                                else -> {
                                    _state.update { it.copy(updateResource = updateResource) }
                                }
                            }
                        }
                    }
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
                    repository.signOut()
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
                                    TAG,
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
    val updateResource: Resource<String> = Resource.Success(""),
    val bioAuthResource: Resource<String> = Resource.Loading(),
    val authResource: Resource<AuthResult> = Resource.Loading(),
    val sendOtpResource: Resource<String> = Resource.Loading(),
    val authType: AuthMethod = AuthMethod.None,
    val signedInUser: UserData? = null,
    val isSignedIn: Boolean = false,

    )



