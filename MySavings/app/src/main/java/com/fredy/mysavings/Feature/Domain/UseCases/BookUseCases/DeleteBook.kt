package com.fredy.mysavings.Feature.Domain.UseCases.BookUseCases

import com.fredy.mysavings.Feature.Domain.Model.Book
import com.fredy.mysavings.Feature.Domain.Repository.BookRepository

class DeleteBook(
    private val repository: BookRepository
) {
    suspend operator fun invoke(book: Book) {
        repository.deleteBook(book)
    }
}