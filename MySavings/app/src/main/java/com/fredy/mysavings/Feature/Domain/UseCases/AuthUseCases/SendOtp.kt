package com.fredy.mysavings.Feature.Domain.UseCases.AuthUseCases

import android.content.Context
import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.fredy.mysavings.MainActivity
import com.fredy.mysavings.Util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.TimeUnit

class SendOtp(
    private val firebaseAuth: FirebaseAuth
) {
    operator fun invoke(
        context: Context,
        phoneNumber: String,
    ): Flow<Resource<String>> = callbackFlow {
        trySend(Resource.Loading())
        val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(
                credential: PhoneAuthCredential
            ) {
                Log.d(
                    "verification completed"
                )
                firebaseAuth.signInWithCredential(
                    credential
                )
            }

            override fun onVerificationFailed(
                p0: FirebaseException
            ) {
                Log.e("verification failed: " + p0)
                p0.message?.let {
                    trySend(Resource.Error(it))
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d("onCodeSent: " + verificationId)
                trySend(
                    Resource.Success(
                        verificationId
                    )
                )
            }
        }

        val options = PhoneAuthOptions.newBuilder(
            firebaseAuth
        ).setPhoneNumber(
            phoneNumber
        ).setTimeout(
            60L, TimeUnit.SECONDS
        ).setActivity(context as MainActivity).setCallbacks(
            callback
        ).build()

        if (options.isNotNull()) {
            Log.d(options.toString())
            PhoneAuthProvider.verifyPhoneNumber(
                options
            )
        }
        awaitClose {}
    }
}