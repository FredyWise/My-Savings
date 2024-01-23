package com.fredy.mysavings.Data.Database.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserData(
    @PrimaryKey
    val firebaseUserId: String = "",
    val username: String? = null,
    val emailOrPhone: String? = null,
    val profilePictureUrl: String? = null,
    val userCurrency: String = "USD",
)