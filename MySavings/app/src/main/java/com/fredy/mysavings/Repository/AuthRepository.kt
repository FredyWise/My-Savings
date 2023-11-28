package com.fredy.mysavings.Repository

import android.util.Log
import com.fredy.mysavings.Data.Database.Entity.UserData
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.TAG
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException
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
    suspend fun signOut()
    suspend fun getCurrentUser(): UserData?

}

class AuthRepositoryImpl @Inject constructor(
    private val oneTapClient: SignInClient,
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


    override suspend fun signOut() {
        try {
            oneTapClient.signOut().await()
            firebaseAuth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    override suspend fun getCurrentUser() = Firebase.firestore.collection(
        "user"
    ).document(
        firebaseAuth.currentUser?.uid ?: "-1"
    ).get().await().toObject<UserData>()
}
