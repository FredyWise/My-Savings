package com.fredy.mysavings.Feature.Domain.UseCases.CategoryUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Domain.Model.Category
import com.fredy.mysavings.Feature.Domain.Repository.AuthRepository
import com.fredy.mysavings.Feature.Domain.Repository.CategoryRepository

class UpsertCategory(
    private val repository: CategoryRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(category: Category): String {
        val currentUser = authRepository.getCurrentUser()!!
        val currentUserId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
        return repository.upsertCategory(category.copy(userIdFk = currentUserId))
    }
}


