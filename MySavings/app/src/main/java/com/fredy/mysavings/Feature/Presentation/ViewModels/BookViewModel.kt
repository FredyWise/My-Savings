package com.fredy.mysavings.Feature.Presentation.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Feature.Domain.Model.Book
import com.fredy.mysavings.Feature.Data.Enum.SortType
import com.fredy.mysavings.Feature.Domain.UseCases.BookUseCases.BookUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.RecordUseCases
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.fredy.mysavings.Feature.Presentation.ViewModels.Event.BookEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val bookUseCases: BookUseCases,
    private val recordUseCases: RecordUseCases
): ViewModel() {

    private val _sortType = MutableStateFlow(
        SortType.ASCENDING
    )

    private val _state = MutableStateFlow(
        BookState()
    )

    private val _bookResource = bookUseCases.getUserBooks().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Resource.Success(emptyList())
    )


    val state = combine(
        _state, _sortType, _bookResource,
    ) { state, sortType, bookResource,  ->
        state.copy(
            bookResource = bookResource,
            sortType = sortType,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        BookState()
    )


    fun onEvent(event: BookEvent) {
        when (event) {
            is BookEvent.ShowDialog -> {
                _state.update {
                    it.copy(
                        bookId = event.book.bookId,
                        bookName = event.book.bookName,
                        bookIconDescription = event.book.bookIconDescription,
                        bookIcon = event.book.bookIcon,
                        book = event.book,
                        isAddingBook = true
                    )
                }
            }

            is BookEvent.HideDialog -> {
                _state.update {
                    it.copy(
                        isAddingBook = false
                    )
                }
            }

            is BookEvent.DeleteBook -> {
                viewModelScope.launch {
                    bookUseCases.deleteBook(
                        event.book
                    )
                    recordUseCases.updateRecordItemWithDeletedBook(event.book)
                    event.onDeleteEffect()
                }
            }

            is BookEvent.SaveBook -> {
                val bookId = state.value.bookId
                val bookName = state.value.bookName
                val bookIcon = state.value.bookIcon
                val bookIconDescription = state.value.bookIconDescription

                if (bookName.isBlank() || bookIcon == 0 || bookIconDescription.isBlank()) {
                    return
                }

                val book = Book(
                    bookId = bookId,
                    bookName = bookName,
                    bookIconDescription = bookIconDescription,
                    bookIcon = bookIcon,
                )
                viewModelScope.launch {
                    bookUseCases.upsertBook(
                        book
                    )
                }
                _state.update { BookState() }
            }

            is BookEvent.BookName -> {
                _state.update {
                    it.copy(
                        bookName = event.bookName
                    )
                }
            }

            is BookEvent.BookIcon -> {
                _state.update {
                    it.copy(
                        bookIcon = event.icon,
                        bookIconDescription = event.iconDescription
                    )
                }
            }

//            is BookEvent.GetBookDetail -> {
//                _state.update {
//                    it.copy(
//                        book = event.book
//                    )
//                }
//            }
//
//            is BookEvent.SortBook -> {
//                _sortType.value = event.sortType
//            }

        }
    }
}

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
