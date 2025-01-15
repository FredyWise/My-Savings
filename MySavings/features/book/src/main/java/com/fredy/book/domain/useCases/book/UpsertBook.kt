package com.fredy.book.domain.useCases.book

import com.fredy.book.domain.BookSpecificRepository
import com.fredy.domain.model.Book

import com.fredy.domain.repository.UserRepository

class UpsertBook(
    private val repository: BookSpecificRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(book: Book): String {
        val currentUser = userRepository.getCurrentUser()
        val currentUserId = currentUser?.firebaseUserId ?: ""
        return repository.upsertBook(book.copy(userIdFk = currentUserId))
    }
}