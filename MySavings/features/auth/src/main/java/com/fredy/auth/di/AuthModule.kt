package com.fredy.auth.di

import com.fredy.auth.domain.AuthUseCases
import com.fredy.auth.domain.BioAuth
import com.fredy.auth.domain.EmailSignIn
import com.fredy.auth.domain.GoogleSignIn
import com.fredy.auth.domain.RegisterUser
import com.fredy.auth.domain.SendOtp
import com.fredy.auth.domain.SignOut
import com.fredy.auth.domain.UpdateUserInformation
import com.fredy.auth.domain.VerifyPhoneNumber
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    @Singleton
    fun provideAuthUseCases(
        firebaseAuth: FirebaseAuth,
    ): AuthUseCases = AuthUseCases(
        updateUserInformation = UpdateUserInformation(
            firebaseAuth
        ),
        googleSignIn = GoogleSignIn(firebaseAuth),
        emailSignIn = EmailSignIn(firebaseAuth),
        registerUser = RegisterUser(firebaseAuth),
        sendOtp = SendOtp(firebaseAuth),
        verifyPhoneNumber = VerifyPhoneNumber(
            firebaseAuth
        ),
        signOut = SignOut(firebaseAuth),
        bioAuth = BioAuth()
    )
}