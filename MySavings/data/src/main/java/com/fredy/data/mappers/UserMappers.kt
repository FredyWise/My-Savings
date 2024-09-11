package com.fredy.data.mappers


import com.google.firebase.auth.FirebaseUser
import com.fredy.data.database.dto.UserData as DataUser
import com.fredy.domain.model.UserData as DomainUser

fun FirebaseUser.toUser(): DomainUser {
    return DomainUser(
        firebaseUserId = uid,
        username = displayName,
        email = email,
        phone = phoneNumber,
        profilePictureUrl = photoUrl.toString(),
    )
}


fun DomainUser.toDataUser(): DataUser {
    return DataUser(
        firebaseUserId,
        username,
        email,
        phone,
        profilePictureUrl,
        userCurrency,
    )
}

fun DataUser.toDomainUser(): DomainUser {
    return DomainUser(
        firebaseUserId,
        username,
        email,
        phone,
        profilePictureUrl,
        userCurrency,
    )
}

fun List<DataUser>.toDomainUser():List<DomainUser>{
    return this.map { it.toDomainUser() }
}

fun List<DomainUser>.toDataUser():List<DataUser>{
    return this.map { it.toDataUser() }
}