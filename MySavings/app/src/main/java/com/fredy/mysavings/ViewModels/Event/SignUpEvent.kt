package com.fredy.mysavings.ViewModels.Event

import com.google.firebase.auth.AuthCredential


sealed interface SignUpEvent {
    data class googleSignIn(val credential: AuthCredential): SignUpEvent
    data class registerUser(
        val email: String,
        val username: String,
        val password: String
    ): SignUpEvent
}