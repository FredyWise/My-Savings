package com.fredy.mysavings.Data.Database.Entity

data class UserData(
    val firebaseUserId: String = "",
    val username: String? = null,
    val emailOrPhone: String? = null,
    val profilePictureUrl: String? = null,
    val userCurrency: String = "USD",
)