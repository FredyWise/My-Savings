package com.fredy.mysavings.Feature.Presentation.ViewModels.BookViewModel

import com.fredy.domain.enums.SortType
import com.fredy.domain.model.Book

data class BookState(
    val bookResource: Resource<List<Book>> = Resource.Loading(),
    val book: Book = Book(),
    val bookId: String = "",
    val bookName: String = "",
    val bookIcon: Int = 0,
    val bookIconDescription: String = "",
    val isAddingBook: Boolean = false,
//    val searchQuery: String = "",
//    val isSearching: Boolean = false,
    val sortType: SortType = SortType.ASCENDING
)