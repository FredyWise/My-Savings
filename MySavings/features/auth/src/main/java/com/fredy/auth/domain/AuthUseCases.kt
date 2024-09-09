package com.fredy.auth.domain



data class AuthUseCases(
    val updateUserInformation: UpdateUserInformation,
    val googleSignIn: GoogleSignIn,
    val emailSignIn: EmailSignIn,
    val registerUser: RegisterUser,
    val sendOtp: SendOtp,
    val verifyPhoneNumber: VerifyPhoneNumber,
    val signOut: SignOut,
    val bioAuth: BioAuth,
)

