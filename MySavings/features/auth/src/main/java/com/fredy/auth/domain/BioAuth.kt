package com.fredy.auth.domain

import android.content.Context
import android.hardware.biometrics.BiometricPrompt
import android.os.CancellationSignal
import androidx.core.content.ContextCompat
import com.fredy.core.util.resource.DataError
import com.fredy.core.util.resource.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class BioAuth() {
    operator fun invoke(context: Context): Flow<Resource<String, DataError.Authentication>> {
        return flow {
            var result: Resource<String, DataError.Authentication> = Resource.Loading()
            emit(result)
            val title = "Login"
            val subtitle = "Login into your account"
            val description =
                "Put your finger on the fingerprint sensor or scan your face to authorise your account."
            val negativeText = "Cancel"
            val executor = ContextCompat.getMainExecutor(
                context
            )
            val biometricPrompt = BiometricPrompt.Builder(
                context
            ).apply {
                setTitle(title)
                setSubtitle(subtitle)
                setDescription(
                    description
                )
                setConfirmationRequired(
                    false
                )
                setNegativeButton(
                    negativeText, executor
                ) { _, _ ->
                    result = Resource.Error(
                        DataError.Authentication.USER_CANCELLED
                    )
                }
            }.build()
            if (result is Resource.Error) emit(
                result
            )

            biometricPrompt.authenticate(
                CancellationSignal(),
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence
                    ) {
                        super.onAuthenticationError(
                            errorCode, errString
                        )
                        Timber.e(
                            "onAuthenticationError:\n error code:$errorCode\n $errString",
                        )
                        result = Resource.Error(
                            DataError.Authentication.UNKNOWN
                        )
                    }

                    override fun onAuthenticationSucceeded(
                        authResult: BiometricPrompt.AuthenticationResult
                    ) {
                        super.onAuthenticationSucceeded(
                            authResult
                        )
                        result = Resource.Success(
                            "Bio Auth Success"
                        )

                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        result = Resource.Error(
                            DataError.Authentication.INVALID_CREDENTIALS
                        )

                    }
                },
            )
            emit(
                result
            )
        }.catch {
            emit(
                Resource.Error(
                    DataError.Authentication.UNKNOWN
                )
            )
            Timber.e(
                it.message.toString()
            )
        }
    }
}