package com.fredy.book.domain.useCases.book

import com.fredy.book.domain.BookSpecificRepository
import com.fredy.domain.model.Book

import kotlinx.coroutines.flow.Flow

class GetBook(
    private val repository: BookSpecificRepository
) {
    operator fun invoke(bookId: String): Flow<Book> {
        return repository.getBook(bookId)
    }
}