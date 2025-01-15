package com.fredy.book.domain.useCases.book

import com.fredy.book.domain.BookSpecificRepository
import com.fredy.domain.model.Book


class DeleteBook(
    private val repository: BookSpecificRepository
) {
    suspend operator fun invoke(book: Book) {
        repository.deleteBook(book)
    }
}