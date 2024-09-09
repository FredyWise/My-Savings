package com.fredy.mysavings.Feature.Domain.UseCases.BookUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.domain.model.Book
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.repository.BookRepository

class UpsertBook(
    private val repository: BookRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(book: Book): String {
        val currentUser = userRepository.getCurrentUser()!!
        val currentUserId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
        return repository.upsertBook(book.copy(userIdFk = currentUserId))
    }
}