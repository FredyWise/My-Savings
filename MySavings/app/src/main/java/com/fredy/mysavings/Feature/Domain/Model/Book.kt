package com.fredy.mysavings.Feature.Domain.Model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fredy.mysavings.Feature.Presentation.Util.DefaultData
import kotlinx.parcelize.Parcelize

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
