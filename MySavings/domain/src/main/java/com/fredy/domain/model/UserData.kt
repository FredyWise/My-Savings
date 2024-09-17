package com.fredy.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.auth.FirebaseUser

@Entity
data class UserData(
    @PrimaryKey
    val firebaseUserId: String = "",
    val username: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val profilePictureUrl: String? = null,
    val userCurrency: String = "USD",
)

fun FirebaseUser.toUser(): UserData {
    return UserData(
        firebaseUserId = uid,
        username = displayName,
        email = email,
        phone = phoneNumber,
        profilePictureUrl = photoUrl.toString(),
    )
}
