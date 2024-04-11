package com.fredy.mysavings.ViewModels.Event

import com.fredy.mysavings.Feature.Data.Database.Model.Book
import com.fredy.mysavings.Feature.Data.Database.Model.Category
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Data.Enum.SortType

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
