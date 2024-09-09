package com.fredy.mysavings.Feature.Domain.UseCases.AuthUseCases

import com.fredy.auth.useCases.GoogleSignIn
import com.fredy.auth.useCases.LoginUser
import com.fredy.auth.useCases.RegisterUser
import com.fredy.auth.useCases.SignOut
import com.fredy.auth.useCases.UpdateUserInformation
import com.fredy.auth.useCases.VerifyPhoneNumber


data class AuthUseCases(
    val loginUser: LoginUser,
    val registerUser: RegisterUser,
    val updateUserInformation: UpdateUserInformation,
    val googleSignIn: GoogleSignIn,
    val sendOtp: SendOtp,
    val verifyPhoneNumber: VerifyPhoneNumber,
    val signOut: SignOut,
)

