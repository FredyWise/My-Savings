package com.fredy.mysavings.Feature.Domain.UseCases.BookUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Domain.Model.Book
import com.fredy.mysavings.Feature.Domain.Repository.AuthRepository
import com.fredy.mysavings.Feature.Domain.Repository.BookRepository

class UpsertBook(
    private val repository: BookRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(book: Book): String {
        val currentUser = authRepository.getCurrentUser()!!
        val currentUserId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
        return repository.upsertBook(book.copy(userIdFk = currentUserId))
    }
}