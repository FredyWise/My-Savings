package com.fredy.domain.useCases.CategoryUseCases

import com.fredy.domain.model.Category
import com.fredy.domain.repository.CategoryRepository
import com.fredy.domain.repository.UserRepository

class UpsertCategory(
    private val repository: CategoryRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(category: Category): String {
        val currentUser = userRepository.getCurrentUser()!!
        val currentUserId = currentUser?.firebaseUserId ?: ""
        return repository.upsertCategory(category.copy(userIdFk = currentUserId))
    }
}


