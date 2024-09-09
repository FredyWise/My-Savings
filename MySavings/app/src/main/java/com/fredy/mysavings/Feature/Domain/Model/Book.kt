package com.fredy.mysavings.Feature.Domain.Model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fredy.domain.model.TrueRecord
import com.fredy.mysavings.Feature.Presentation.Util.DefaultData
import kotlinx.parcelize.Parcelize
import java.time.LocalDate


@Entity
@Parcelize
data class Book(
    @PrimaryKey
    var bookId: String = "",
    val userIdFk: String = "",
    val bookName: String = "DefaultBook",
    val bookIcon: Int = DefaultData.bookInitIcon.image,
    val bookIconDescription: String = DefaultData.bookInitIcon.description,
): Parcelable{
    fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            "$bookName",
            "${bookName.first()}",
        )

        return matchingCombinations.any {
            it.contains(query, ignoreCase = true)
        }
    }
}


//open class Searchable {
//    open fun doesMatchSearchQuery(query: String, fields: List<String>): Boolean {
//        return fields.any { it.contains(query, ignoreCase = true) }
//    }
//}
//
//
//
//@Entity
//@Parcelize
//open class Book(
//    @PrimaryKey
//    var bookId: String = "",
//    val userIdFk: String = "",
//    val bookName: String = "DefaultBook",
//    val bookIcon: Int = DefaultData.bookInitIcon.image,
//    val bookIconDescription: String = DefaultData.bookInitIcon.description,
//) : Parcelable, Searchable() {
//     open fun doesMatchSearchQuery(query: String): Boolean {
//        val fieldsToSearch = listOf(bookName, bookIconDescription)
//        return super.doesMatchSearchQuery(query, fieldsToSearch)
//    }
//}



