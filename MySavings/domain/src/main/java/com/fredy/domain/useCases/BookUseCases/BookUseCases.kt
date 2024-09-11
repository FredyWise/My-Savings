package com.fredy.domain.useCases.BookUseCases

import com.fredy.mysavings.Feature.Domain.UseCases.BookUseCases.DeleteBook
import com.fredy.mysavings.Feature.Domain.UseCases.BookUseCases.GetBook
import com.fredy.mysavings.Feature.Domain.UseCases.BookUseCases.UpsertBook

data class BookUseCases(
    val upsertBook: UpsertBook,
    val deleteBook: DeleteBook,
    val getBook: GetBook,
    val getUserBooks: GetUserBooks
)


