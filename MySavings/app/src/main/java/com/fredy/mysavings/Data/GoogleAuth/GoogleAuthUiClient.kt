package com.fredy.mysavings.Data.GoogleAuth

import android.content.Context
import com.fredy.mysavings.Data.Database.Entity.UserData
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

class GoogleAuthUiClient (
    private val context: Context,
    private val oneTapClient: SignInClient
) {

    suspend fun signOut(auth: FirebaseAuth) {
        try {
            oneTapClient.signOut().await()
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    fun getSignedInUser(auth: FirebaseAuth): UserData? = auth.currentUser?.run {
        UserData(
            firebaseUserId = uid,
            username = displayName,
            email = email,
            profilePictureUrl = photoUrl?.toString()
        )
    }

}