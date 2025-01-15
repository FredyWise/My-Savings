package com.fredy.domain.di

import com.fredy.domain.repository.UserRepository
import com.fredy.domain.userUseCases.DeleteUser
import com.fredy.domain.userUseCases.GetAllUsersOrderedByName
import com.fredy.domain.userUseCases.GetCurrentUser
import com.fredy.domain.userUseCases.GetUser
import com.fredy.domain.userUseCases.InsertUser
import com.fredy.domain.userUseCases.SearchUsers
import com.fredy.domain.userUseCases.UpdateUser
import com.fredy.domain.userUseCases.UploadProfilePicture
import com.fredy.domain.userUseCases.UserUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCasesModule {


    @Provides
    @Singleton
    fun provideUserUseCases(
        userRepository: UserRepository
    ): UserUseCases = UserUseCases(
        insertUser = InsertUser(userRepository),
        updateUser = UpdateUser(userRepository),
        deleteUser = DeleteUser(userRepository),
        getUser = GetUser(userRepository),
        getCurrentUser = GetCurrentUser(userRepository),
        getAllUsersOrderedByName = GetAllUsersOrderedByName(userRepository),
        searchUsers = SearchUsers(userRepository),
        uploadProfilePicture = UploadProfilePicture()
    )


}