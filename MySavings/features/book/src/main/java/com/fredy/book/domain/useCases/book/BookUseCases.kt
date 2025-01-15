package com.fredy.book.domain.useCases.book

data class BookUseCases(
    val upsertBook: UpsertBook,
    val deleteBook: DeleteBook,
    val getBook: GetBook,
    val getUserBooks: GetUserBooks,
    val updateRecordItemWithDeletedBook: UpdateRecordItemWithDeletedBook,
)


