package com.fredy.mysavings.Feature.Domain.UseCases.BookUseCases

import com.fredy.mysavings.Feature.Domain.Model.Book
import com.fredy.mysavings.Feature.Domain.Repository.BookRepository
import kotlinx.coroutines.flow.Flow

class GetBook(
    private val repository: BookRepository
) {
    operator fun invoke(bookId: String): Flow<Book> {
        return repository.getBook(bookId)
    }
}