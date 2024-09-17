package com.fredy.domain.mappers.recordUIMapper

import com.fredy.domain.enums.SortType
import com.fredy.domain.model.Book
import com.fredy.domain.model.BookMap
import com.fredy.domain.model.TrueRecord

fun List<TrueRecord>.toBookSortedMaps(
    books: List<Book>,
    sortType: SortType = SortType.DESCENDING
): List<BookMap> {
    return books.map { book ->
        val records = this.filter { it.record.bookIdFk == book.bookId }
        BookMap(
            book = book,
            recordMaps = records.toRecordSortedMaps(sortType)
        )
    }
}
