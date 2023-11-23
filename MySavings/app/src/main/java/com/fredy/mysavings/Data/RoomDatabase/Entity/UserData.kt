package com.fredy.mysavings.Data.RoomDatabase.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserData(
    @PrimaryKey(autoGenerate = false)
    val firebaseUserId: String = "",
    val username: String? = null,
    val email: String? = null,
    val profilePictureUrl: String? = null,
    val userCurrency:String = "",
)