package com.fredy.mysavings.ViewModels.Event

import android.net.Uri
import com.google.firebase.auth.AuthCredential


sealed interface AuthEvent {
    data class googleAuth(val credential: AuthCredential): AuthEvent
    data class loginUser(
        val email: String,
        val password: String,
    ): AuthEvent

    data class registerUser(
        val email: String,
        val username: String,
        val password: String,
        val photoUrl: Uri,
    ): AuthEvent

    object signOut: AuthEvent

}