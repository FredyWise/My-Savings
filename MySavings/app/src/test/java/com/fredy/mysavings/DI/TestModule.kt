package com.fredy.mysavings.DI

import android.app.PendingIntent
import android.content.Intent
import com.google.android.datatransport.runtime.dagger.Module
import com.google.android.datatransport.runtime.dagger.Provides
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.auth.api.identity.zbu
import com.google.android.gms.common.api.internal.ApiKey
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.interop.InteropAppCheckTokenProvider
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.heartbeatinfo.HeartBeatController
import com.google.firebase.inject.Provider
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.Executor
import java.util.concurrent.ScheduledExecutorService
import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//object TestModule {
//
//    @Provides
//    @Singleton
//    fun providesSignInClient(): SignInClient = FakeSignInClient()
//
//    @Provides
//    @Singleton
//    fun provideAuth(): FirebaseAuth = FakeFirebaseAuth()
//}
//
//class FakeFirebaseAuth(
//    p0: FirebaseApp,
//    p1: Provider<InteropAppCheckTokenProvider>,
//    p2: Provider<HeartBeatController>,
//    p3: Executor,
//    p4: Executor,
//    p5: Executor,
//    p6: ScheduledExecutorService,
//    p7: Executor
//) : FirebaseAuth(p0, p1, p2, p3, p4, p5, p6, p7)  {
//    // You can add a fake user to simulate the behavior of a real FirebaseAuth instance.
//    private val fakeUser = FirebaseUser()
//
//    override fun signInWithCredential(credential: AuthCredential): Task<AuthResult> {
//        return Tasks.forResult(AuthResult(fakeUser))
//    }
//
//    override fun signInWithEmailAndPassword(email: String, password: String): Task<AuthResult> {
//        return Tasks.forResult(AuthResult(fakeUser))
//    }
//
//    override fun createUserWithEmailAndPassword(email: String, password: String): Task<AuthResult> {
//        return Tasks.forResult(AuthResult(fakeUser))
//    }
//
//    override fun signOut() {
//        // Do nothing for testing.
//    }
//
//    override fun getCurrentUser(): FirebaseUser? {
//        return fakeUser
//    }
//}
//
//class FakeSignInClient : SignInClient {
//    override fun getApiKey(): ApiKey<zbu> {
//        TODO("Not yet implemented")
//    }
//
//    override fun getSignInCredentialFromIntent(p0: Intent?): SignInCredential {
//        TODO("Not yet implemented")
//    }
//
//    override fun beginSignIn(p0: BeginSignInRequest): Task<BeginSignInResult> {
//        TODO("Not yet implemented")
//    }
//
//    override fun getPhoneNumberHintIntent(p0: GetPhoneNumberHintIntentRequest): Task<PendingIntent> {
//        TODO("Not yet implemented")
//    }
//
//    override fun getSignInIntent(p0: GetSignInIntentRequest): Task<PendingIntent> {
//        TODO("Not yet implemented")
//    }
//
//    override fun signOut(): Task<Void> {
//        return Tasks.forResult(null)
//    }
//
//    override fun getPhoneNumberFromIntent(p0: Intent?): String {
//        TODO("Not yet implemented")
//    }
//}
