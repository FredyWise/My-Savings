package com.fredy.mysavings.Feature.Presentation.ViewModels.BookViewModel

import com.fredy.mysavings.Feature.Domain.Model.Book

sealed interface BookEvent {
    object SaveBook: BookEvent
    data class BookName(val bookName: String): BookEvent
    data class BookIcon(
        val icon: Int,
        val iconDescription: String
    ): BookEvent
    data class ShowDialog(val book: Book): BookEvent
    object HideDialog: BookEvent
    data class DeleteBook(val book: Book, val onDeleteEffect: ()->Unit): BookEvent
//    data class GetBookDetail(val book: Book): BookEvent

}
