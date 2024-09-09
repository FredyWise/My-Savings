package com.fredy.auth.domain

import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber
import kotlin.coroutines.cancellation.CancellationException

class SignOut(
    private val firebaseAuth: FirebaseAuth,
//    private val oneTapClient: SignInClient
) {
    suspend operator fun invoke() {
        try {
//            oneTapClient.signOut().await()
            firebaseAuth.signOut()
            Timber.d("signOut: " + firebaseAuth.currentUser)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }
}