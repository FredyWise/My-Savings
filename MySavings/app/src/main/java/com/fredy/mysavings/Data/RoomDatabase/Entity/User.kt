package com.fredy.mysavings.Data.RoomDatabase.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserData(
    @PrimaryKey
    val firebaseUserId: String,
    val username: String?,
    val email: String?,
    val profilePictureUrl: String?,
    val userCurrency:String = "",
)