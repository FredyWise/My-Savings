package com.fredy.mysavings.Feature.Domain.UseCases.AuthUseCases


data class AuthUseCases(
    val loginUser: LoginUser,
    val registerUser: RegisterUser,
    val updateUserInformation: UpdateUserInformation,
    val googleSignIn: GoogleSignIn,
    val sendOtp: SendOtp,
    val verifyPhoneNumber: VerifyPhoneNumber,
    val signOut: SignOut,
)

