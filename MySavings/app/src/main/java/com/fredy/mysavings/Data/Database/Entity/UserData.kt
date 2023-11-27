package com.fredy.mysavings.Data.Database.Entity

data class UserData(
    val firebaseUserId: String = "",
    val username: String? = null,
    val email: String? = null,
    val profilePictureUrl: String? = null,
    val userCurrency: String = "",
)