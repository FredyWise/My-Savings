package com.fredy.domain.useCases.UserUseCases

import com.fredy.domain.model.toUser
import com.fredy.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber

class InsertUser(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(firebaseUser: FirebaseUser?) {
        val user = firebaseUser?.toUser()
        user?.let {
            val isExist = userRepository.getUser(user.firebaseUserId).firstOrNull()
            Timber.i("InsertUser.Data: $isExist")
            if (isExist == null) {
                userRepository.upsertUser(user)
            }
        }
    }
}