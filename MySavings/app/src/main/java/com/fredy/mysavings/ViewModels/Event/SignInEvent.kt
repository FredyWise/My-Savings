package com.fredy.mysavings.ViewModels.Event

import com.google.firebase.auth.AuthCredential


sealed interface SignInEvent {
    data class googleSignIn(val credential: AuthCredential): SignInEvent
    data class loginUser(
        val email: String,
        val password: String
    ): SignInEvent

    object getSignedInUser: SignInEvent

}