package com.fredy.mysavings.Feature.Domain.UseCases.AuthUseCases

import android.net.Uri
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.fredy.mysavings.Util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class UpdateUserInformation(
    private val firebaseAuth: FirebaseAuth
) {
    operator fun invoke(
        profilePicture: Uri?,
        username: String,
        oldPassword: String,
        password: String
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        Log.i("updateUserInformation: starting")
        val successMessage = mutableListOf<String>()
        val errorMessage = mutableListOf<String>()
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(username)
            .setPhotoUri(profilePicture)
            .build()
        val user = firebaseAuth.currentUser!!

        suspendCancellableCoroutine<Unit> { continuation ->
            user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i("Display name updated successfully")
                    successMessage.add("Display Name")
                } else {
                    Log.e("Error updating display name")
                    errorMessage.add("Updating Display Name")
                }
                continuation.resume(Unit)
            }
        }

        if (oldPassword.isNotEmpty()) {
            val credential = EmailAuthProvider.getCredential(user.email ?: "", oldPassword)

            suspendCancellableCoroutine<Unit> { continuation ->
                user.reauthenticate(credential).addOnCompleteListener { reAuthTask ->
                    if (reAuthTask.isSuccessful) {
                        if (password.isNotEmpty()) {
                            user.updatePassword(password)
                                .addOnCompleteListener { passwordUpdateTask ->
                                    if (passwordUpdateTask.isSuccessful) {
                                        Log.i("Password updated successfully")
                                        successMessage.add("Password")
                                    } else {
                                        Log.e("Error updating password")
                                        errorMessage.add("Updating Password")
                                    }
                                }
                        }
                    } else {
                        Log.e("Re-authentication failed")
                        errorMessage.add(": Re-authentication failed")
                    }
                    continuation.resume(Unit)
                }
            }
        }

        emit(
            if (errorMessage.isNotEmpty()) {
                val errorString =
                    errorMessage.joinToString(separator = ", ", limit = errorMessage.size - 1) +
                            (if (errorMessage.size > 1) ", and " else "") + errorMessage.last()
                Resource.Error("Error $errorString")
            } else if (successMessage.isNotEmpty()) {
                val successString =
                    successMessage.joinToString(separator = ", ", limit = successMessage.size - 1) +
                            (if (successMessage.size > 1) ", and " else "") + successMessage.last()
                Resource.Success("$successString updated successfully")
            } else Resource.Error("Unexpected Error")
        )
    }.catch { e ->
        Log.e("Error updating user information: ${e.message}")
        emit(Resource.Error("Error updating user information: ${e.message}"))
    }
}