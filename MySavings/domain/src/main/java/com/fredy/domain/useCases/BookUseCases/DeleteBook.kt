package com.fredy.domain.useCases.BookUseCases

import com.fredy.domain.model.Book
import com.fredy.domain.repository.BookRepository

class DeleteBook(
    private val repository: BookRepository
) {
    suspend operator fun invoke(book: Book) {
        repository.deleteBook(book)
    }
}