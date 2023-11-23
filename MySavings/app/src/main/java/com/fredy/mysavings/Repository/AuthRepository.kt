package com.fredy.mysavings.Repository

import com.fredy.mysavings.Data.GoogleAuth.GoogleAuthUiClient
import com.fredy.mysavings.Data.RoomDatabase.Entity.UserData
import com.fredy.mysavings.Util.Resource
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface AuthRepository {
    fun loginUser(
        email: String,
        password: String
    ): Flow<Resource<AuthResult>>

    fun registerUser(
        email: String,
        password: String
    ): Flow<Resource<AuthResult>>

    fun googleSignIn(credential: AuthCredential): Flow<Resource<AuthResult>>
    fun getSignedInUser(): UserData?
    suspend fun signOut()

}

class AuthRepositoryImpl @Inject constructor(
    private val googleAuthUiClient: GoogleAuthUiClient,
    private val firebaseAuth: FirebaseAuth
): AuthRepository {
    override fun loginUser(
        email: String,
        password: String
    ): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val result = firebaseAuth.signInWithEmailAndPassword(
                email,
                password
            ).await()
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }

    }

    override fun registerUser(
        email: String,
        password: String
    ): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val result = firebaseAuth.createUserWithEmailAndPassword(
                email,
                password
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

    override fun getSignedInUser(): UserData? = googleAuthUiClient.getSignedInUser(
        firebaseAuth
    )

    override suspend fun signOut() {
        googleAuthUiClient.signOut(firebaseAuth)
    }


}