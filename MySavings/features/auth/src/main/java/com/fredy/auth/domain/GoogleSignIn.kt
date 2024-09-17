package com.fredy.auth.domain


import com.fredy.domain.util.resource.DataError
import com.fredy.domain.util.resource.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class GoogleSignIn(
    private val firebaseAuth: FirebaseAuth
) {
    operator fun invoke(credential: AuthCredential): Flow<Resource<AuthResult, DataError.Authentication>> {
        return flow<Resource<AuthResult, DataError.Authentication>> {
            emit(Resource.Loading())
            val result = firebaseAuth.signInWithCredential(
                credential
            ).await()
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(DataError.Authentication.UNKNOWN))
        }
    }
}