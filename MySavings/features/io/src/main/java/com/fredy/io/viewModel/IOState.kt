package com.fredy.io.viewModel

import com.fredy.domain.model.Book
import com.fredy.domain.model.TrueRecord
import com.fredy.io.domain.model.DBInfo
import java.time.LocalDateTime

data class IOState(
    val startDate: LocalDateTime = LocalDateTime.now().minusMonths(1),
    val endDate: LocalDateTime = LocalDateTime.now(),
    val books: List<Book> = emptyList(),
    val currentBook: Book = Book(bookName = ""),
    val exportRecords: List<TrueRecord> = emptyList(),
    val importRecords: List<TrueRecord> = emptyList(),
    val exportConfirmation: Boolean = false,
    val importConfirmation: Boolean = false,
    val updateRecordValue: Boolean = false,
    val dbInfo: DBInfo = DBInfo(),
    val exportDBInfo: DBInfo = DBInfo(),
    val importDBInfo: DBInfo = DBInfo()
)