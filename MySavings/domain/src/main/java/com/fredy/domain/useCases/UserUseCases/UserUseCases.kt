package com.fredy.domain.useCases.UserUseCases

data class UserUseCases(
    val insertUser: InsertUser,
    val updateUser: UpdateUser,
    val deleteUser: DeleteUser,
    val getUser: GetUser,
    val getCurrentUser: GetCurrentUser,
    val getAllUsersOrderedByName: GetAllUsersOrderedByName,
    val searchUsers: SearchUsers,
    val uploadProfilePicture: UploadProfilePicture
)


