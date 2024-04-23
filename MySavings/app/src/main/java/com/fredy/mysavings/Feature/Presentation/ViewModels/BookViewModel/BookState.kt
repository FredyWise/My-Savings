package com.fredy.mysavings.Feature.Presentation.ViewModels.BookViewModel

import com.fredy.mysavings.Feature.Data.Enum.SortType
import com.fredy.mysavings.Feature.Domain.Model.Book
import com.fredy.mysavings.Feature.Domain.Util.Resource

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