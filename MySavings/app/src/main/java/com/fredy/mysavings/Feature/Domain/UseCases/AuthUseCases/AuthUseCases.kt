package com.fredy.mysavings.Feature.Domain.UseCases.AuthUseCases

import android.content.Context
import android.net.Uri
import com.fredy.mysavings.Feature.Domain.Repository.AuthRepository
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

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
    private val authRepository: AuthRepository
) {
    operator fun invoke(email: String, password: String): Flow<Resource<AuthResult>> {
        return authRepository.loginUser(email, password)
    }
}

class RegisterUser(
    private val authRepository: AuthRepository
) {
    operator fun invoke(email: String, password: String): Flow<Resource<AuthResult>> {
        return authRepository.registerUser(email, password)
    }
}

class UpdateUserInformation(
    private val authRepository: AuthRepository
) {
    operator fun invoke(profilePicture: Uri?, username: String, oldPassword: String, password: String): Flow<Resource<String>> {
        return authRepository.updateUserInformation(profilePicture, username, oldPassword, password)
    }
}

class GoogleSignIn(
    private val authRepository: AuthRepository
) {
    operator fun invoke(credential: AuthCredential): Flow<Resource<AuthResult>> {
        return authRepository.googleSignIn(credential)
    }
}

class SendOtp(
    private val authRepository: AuthRepository
) {
    operator fun invoke(context: Context, phoneNumber: String): Flow<Resource<String>> {
        return authRepository.sendOtp(context, phoneNumber)
    }
}

class VerifyPhoneNumber(
    private val authRepository: AuthRepository
) {
    operator fun invoke(context: Context, verificationId: String, code: String): Flow<Resource<AuthResult>> {
        return authRepository.verifyPhoneNumber(context, verificationId, code)
    }
}

class SignOut(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        authRepository.signOut()
    }
}


