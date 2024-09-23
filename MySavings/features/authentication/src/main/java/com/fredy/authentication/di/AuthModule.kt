package com.fredy.authentication.di

import com.fredy.authentication.domain.usecases.AuthUseCases
import com.fredy.authentication.domain.usecases.BioAuth
import com.fredy.authentication.domain.usecases.EmailSignIn
import com.fredy.authentication.domain.usecases.GoogleSignIn
import com.fredy.authentication.domain.usecases.RegisterUser
import com.fredy.authentication.domain.usecases.SendOtp
import com.fredy.authentication.domain.usecases.SignOut
import com.fredy.authentication.domain.usecases.UpdateUserInformation
import com.fredy.authentication.domain.usecases.VerifyPhoneNumber
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