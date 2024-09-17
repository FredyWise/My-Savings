package com.fredy.auth.domain

import com.fredy.domain.util.resource.DataError
import com.fredy.domain.util.resource.Resource
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class EmailSignIn(
    private val firebaseAuth: FirebaseAuth
) {
    operator fun invoke(
        email: String,
        password: String
    ): Flow<Resource<AuthResult, DataError.Authentication>> {
        return flow<Resource<AuthResult, DataError.Authentication>> {
            emit(Resource.Loading())
            val result = firebaseAuth.signInWithEmailAndPassword(
                email, password
            ).await()
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(DataError.Authentication.UNKNOWN))
        }
    }
}