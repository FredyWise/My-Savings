package com.fredy.mysavings.Feature.Domain.UseCases.AuthUseCases

import android.content.Context
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

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