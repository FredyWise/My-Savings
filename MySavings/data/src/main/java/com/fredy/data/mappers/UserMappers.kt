package com.fredy.data.mappers


import com.fredy.domain.model.UserData
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser.toUser(): UserData {
    return UserData(
        firebaseUserId = uid,
        username = displayName,
        email = email,
        phone = phoneNumber,
        profilePictureUrl = photoUrl.toString(),
    )
}

