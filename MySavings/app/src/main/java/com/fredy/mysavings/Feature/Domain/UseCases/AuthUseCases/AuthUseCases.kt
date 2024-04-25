package com.fredy.mysavings.Feature.Domain.UseCases.AuthUseCases

import android.content.Context
import android.net.Uri
import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.fredy.mysavings.MainActivity
import com.fredy.mysavings.Util.Log
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume


data class AuthUseCases(
    val loginUser: LoginUser,
    val registerUser: RegisterUser,
    val updateUserInformation: UpdateUserInformation,
    val googleSignIn: GoogleSignIn,
    val sendOtp: SendOtp,
    val verifyPhoneNumber: VerifyPhoneNumber,
    val signOut: SignOut,
)

class LoginUser(
    private val firebaseAuth: FirebaseAuth
) {
    operator fun invoke(email: String, password: String): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val result = firebaseAuth.signInWithEmailAndPassword(
                email, password
            ).await()
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }
}

class RegisterUser(
    private val firebaseAuth: FirebaseAuth
) {
    operator fun invoke(email: String, password: String): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val result = firebaseAuth.createUserWithEmailAndPassword(
                email, password
            ).await()
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }
}

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

class GoogleSignIn(
    private val firebaseAuth: FirebaseAuth
) {
    operator fun invoke(credential: AuthCredential): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val result = firebaseAuth.signInWithCredential(
                credential
            ).await()
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }
}

class SendOtp(
    private val firebaseAuth: FirebaseAuth
) {
    operator fun invoke(
        context: Context,
        phoneNumber: String,
    ): Flow<Resource<String>> = callbackFlow {
        trySend(Resource.Loading())
        val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(
                credential: PhoneAuthCredential
            ) {
                Log.d(
                    "verification completed"
                )
                firebaseAuth.signInWithCredential(
                    credential
                )
            }

            override fun onVerificationFailed(
                p0: FirebaseException
            ) {
                Log.e("verification failed: " + p0)
                p0.message?.let {
                    trySend(Resource.Error(it))
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d("onCodeSent: " + verificationId)
                trySend(
                    Resource.Success(
                        verificationId
                    )
                )
            }
        }

        val options = PhoneAuthOptions.newBuilder(
            firebaseAuth
        ).setPhoneNumber(
            phoneNumber
        ).setTimeout(
            60L, TimeUnit.SECONDS
        ).setActivity(context as MainActivity).setCallbacks(
            callback
        ).build()

        if (options.isNotNull()) {
            Log.d(options.toString())
            PhoneAuthProvider.verifyPhoneNumber(
                options
            )
        }
        awaitClose {}
    }
}

class VerifyPhoneNumber(
    private val firebaseAuth: FirebaseAuth
) {
    operator fun invoke(
        context: Context,
        verificationId: String,
        code: String
    ): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val credential = PhoneAuthProvider.getCredential(
                verificationId, code
            )
            val result = firebaseAuth.signInWithCredential(
                credential
            ).await()
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }
}

class SignOut(
    private val firebaseAuth: FirebaseAuth,
    private val oneTapClient: SignInClient
) {
    suspend operator fun invoke() {
        try {
            oneTapClient.signOut().await()
            firebaseAuth.signOut()
            Log.d("signOut: " + firebaseAuth.currentUser)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }
}