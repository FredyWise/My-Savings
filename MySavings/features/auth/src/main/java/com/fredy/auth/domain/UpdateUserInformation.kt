package com.fredy.auth.domain

import android.net.Uri
import com.fredy.domain.util.resource.DataError
import com.fredy.domain.util.resource.Resource
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import kotlin.coroutines.resume

class UpdateUserInformation(
    private val firebaseAuth: FirebaseAuth
) {
    operator fun invoke(
        profilePicture: Uri?,
        username: String,
        oldPassword: String,
        password: String
    ): Flow<Resource<String, DataError.Authentication>> =
        flow<Resource<String, DataError.Authentication>> {
            emit(Resource.Loading())
            Timber.i("updateUserInformation: starting")
            val successMessage = mutableListOf<String>()
            val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(
                username
            ).setPhotoUri(profilePicture).build()
            val user = firebaseAuth.currentUser!!

            suspendCancellableCoroutine<Unit> { continuation ->
                user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Timber.i("Display name updated successfully")
                        successMessage.add("Display Name")
                    } else {
                        Timber.e("Error updating display name")
                        throw Exception(DataError.Authentication.UPDATE_DISPLAY_NAME_FAILED.name)
                    }
                    continuation.resume(Unit)
                }
            }

            if (oldPassword.isNotEmpty()) {
                val credential = EmailAuthProvider.getCredential(
                    user.email ?: "",
                    oldPassword
                )

                suspendCancellableCoroutine<Unit> { continuation ->
                    user.reauthenticate(credential).addOnCompleteListener { reAuthTask ->
                        if (reAuthTask.isSuccessful) {
                            if (password.isNotEmpty()) {
                                user.updatePassword(
                                    password
                                ).addOnCompleteListener { passwordUpdateTask ->
                                    if (passwordUpdateTask.isSuccessful) {
                                        Timber.i("Password updated successfully")
                                        successMessage.add(
                                            "Password"
                                        )
                                    } else {
                                        Timber.e("Error updating password")
                                        throw Exception(
                                            DataError.Authentication.UPDATE_PASSWORD_FAILED.name
                                        )
                                    }
                                }
                            }
                        } else {
                            Timber.e("Re-authentication failed")
                            throw Exception(DataError.Authentication.REAUTHENTICATION_FAILED.name)
                        }
                        continuation.resume(Unit)
                    }
                }
            }

            emit(
                if (successMessage.isNotEmpty()) {
                    val successString = successMessage.joinToString(
                        separator = ", ",
                        limit = successMessage.size - 1
                    ) + (if (successMessage.size > 1) ", and " else "") + successMessage.last()
                    Resource.Success("$successString updated successfully")
                } else Resource.Error(DataError.Authentication.UNKNOWN)
            )
        }.catch { e ->
            Timber.e("Error updating user information: ${e.message}")

            when (e.message) {
                DataError.Authentication.REAUTHENTICATION_FAILED.name -> {
                    emit(Resource.Error(DataError.Authentication.REAUTHENTICATION_FAILED))
                }

                DataError.Authentication.UPDATE_DISPLAY_NAME_FAILED.name -> {
                    emit(Resource.Error(DataError.Authentication.UPDATE_DISPLAY_NAME_FAILED))
                }

                DataError.Authentication.UPDATE_PASSWORD_FAILED.name -> {
                    emit(Resource.Error(DataError.Authentication.UPDATE_PASSWORD_FAILED))
                }

                else -> {
                    emit(Resource.Error(DataError.Authentication.UNKNOWN))
                }
            }
        }
}