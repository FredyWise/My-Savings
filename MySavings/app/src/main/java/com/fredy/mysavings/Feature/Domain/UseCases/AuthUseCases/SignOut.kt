package com.fredy.mysavings.Feature.Domain.UseCases.AuthUseCases

import com.fredy.mysavings.Util.Log
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class SignOut(
    private val firebaseAuth: FirebaseAuth,
    private val oneTapClient: SignInClient
) {
    suspend operator fun invoke() {
        try {
            oneTapClient.signOut().await()
            firebaseAuth.signOut()
            Log.d("signOut: " + firebaseAuth.currentUser)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }
}