package com.fredy.mysavings.ViewModels.Event

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.AuthCredential


sealed interface AuthEvent {
    data class GoogleAuth(val credential: AuthCredential): AuthEvent
    data class LoginUser(
        val email: String,
        val password: String,
    ): AuthEvent

    data class SendOtp(
        val context: Context,
        val phoneNumber: String,
        val onCodeSent: () -> Unit
    ): AuthEvent

    data class VerifyPhoneNumber(
        val context: Context,
        val code: String
    ): AuthEvent

    data class RegisterUser(
        val email: String,
        val username: String,
        val password: String,
        val photoUrl: Uri,
    ): AuthEvent

    data class UpdateUserData(
//        val email: String,
        val username: String,
        val oldPassword: String,
        val password: String,
        val photoUrl: Uri,
    ): AuthEvent

    object BioAuth: AuthEvent

    object SignOut: AuthEvent
    object GetCurrentUser: AuthEvent


}