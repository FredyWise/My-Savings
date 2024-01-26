package com.fredy.mysavings.Data.Repository

import android.content.Context
import android.util.Log
import com.fredy.mysavings.Data.Database.Model.UserData
import com.fredy.mysavings.MainActivity
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.TAG
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface AuthRepository {
    fun loginUser(
        email: String, password: String
    ): Flow<Resource<AuthResult>>

    fun registerUser(
        email: String,
        password: String,
    ): Flow<Resource<AuthResult>>

    fun googleSignIn(credential: AuthCredential): Flow<Resource<AuthResult>>

    fun sendOtp(
        context: Context,
        phoneNumber: String,
    ): Flow<Resource<String>>

    fun verifyPhoneNumber(
        context: Context,
        verificationId: String,
        code: String,
    ): Flow<Resource<AuthResult>>

    suspend fun signOut()
    suspend fun getCurrentUser(): UserData?

}

class AuthRepositoryImpl @Inject constructor(
    private val oneTapClient: SignInClient,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
): AuthRepository {
    override fun loginUser(
        email: String, password: String
    ): Flow<Resource<AuthResult>> {
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

    override fun registerUser(
        email: String,
        password: String,
    ): Flow<Resource<AuthResult>> {
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

    override fun googleSignIn(credential: AuthCredential): Flow<Resource<AuthResult>> {
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

    override fun sendOtp(
        context: Context,
        phoneNumber: String,
    ): Flow<Resource<String>> = callbackFlow {
        trySend(Resource.Loading())
        val callback = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(
                credential: PhoneAuthCredential
            ) {
                Log.d(
                    TAG, "verification completed"
                )
                firebaseAuth.signInWithCredential(
                    credential
                )
            }

            override fun onVerificationFailed(
                p0: FirebaseException
            ) {
                Log.e(
                    TAG,
                    "verification failed: " + p0
                )
                p0.message?.let {
                    trySend(Resource.Error(it))
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(
                    TAG,
                    "onCodeSent: " + verificationId
                )
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

        if (options != null) {
            Log.d(TAG, options.toString())
            PhoneAuthProvider.verifyPhoneNumber(
                options
            )
        }
        awaitClose {}
    }


    override fun verifyPhoneNumber(
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


    override suspend fun signOut() {
        try {
            oneTapClient.signOut().await()
            firebaseAuth.signOut()
            Log.d(TAG, "signOut: "+firebaseAuth.currentUser)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    override suspend fun getCurrentUser() = firestore.collection(
        "user"
    ).document(
        firebaseAuth.currentUser?.uid ?: "-1"
    ).get().await().toObject<UserData>()
}
