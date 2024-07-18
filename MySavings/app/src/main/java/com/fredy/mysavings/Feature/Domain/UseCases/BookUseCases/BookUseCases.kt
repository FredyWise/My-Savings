package com.fredy.mysavings.Feature.Domain.UseCases.BookUseCases

data class BookUseCases(
    val upsertBook: UpsertBook,
    val deleteBook: DeleteBook,
    val getBook: GetBook,
    val getUserBooks: GetUserBooks
)


