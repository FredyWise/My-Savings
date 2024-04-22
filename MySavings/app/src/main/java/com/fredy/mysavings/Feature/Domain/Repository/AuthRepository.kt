package com.fredy.mysavings.Feature.Domain.Repository

import android.content.Context
import android.net.Uri
import com.fredy.mysavings.Feature.Domain.Model.UserData
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    // this is should be a service that will need user repo
    fun loginUser(
        email: String, password: String
    ): Flow<Resource<AuthResult>>

    fun registerUser(
        email: String,
        password: String,
    ): Flow<Resource<AuthResult>>

    fun updateUserInformation(
        profilePicture: Uri?,
        username: String,
//        email: String,
        oldPassword: String,
        password: String,
    ): Flow<Resource<String>>

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

