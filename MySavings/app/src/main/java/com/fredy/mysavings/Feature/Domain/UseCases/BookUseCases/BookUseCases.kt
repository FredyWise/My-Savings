package com.fredy.mysavings.Feature.Domain.UseCases.BookUseCases

import com.fredy.domain.useCases.BookUseCases.DeleteBook
import com.fredy.domain.useCases.BookUseCases.GetBook
import com.fredy.domain.useCases.BookUseCases.UpsertBook

data class BookUseCases(
    val upsertBook: UpsertBook,
    val deleteBook: DeleteBook,
    val getBook: GetBook,
    val getUserBooks: GetUserBooks
)


