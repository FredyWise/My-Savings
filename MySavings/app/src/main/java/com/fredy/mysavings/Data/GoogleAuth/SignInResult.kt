package com.fredy.mysavings.Data.GoogleAuth

import com.fredy.mysavings.Data.RoomDatabase.Entity.UserData

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)