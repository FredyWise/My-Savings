package com.fredy.book.data


import com.fredy.data.database.dto.Book as DataBook
import com.fredy.domain.model.Book as DomainBook


fun DomainBook.toDataBook(): DataBook {
    return DataBook(
        bookId,
        userIdFk,
        bookName,
        bookIcon,
        bookIconDescription,
    )
}

fun DataBook.toDomainBook(): DomainBook {
    return DomainBook(
        bookId,
        userIdFk,
        bookName,
        bookIcon,
        bookIconDescription,
    )
}