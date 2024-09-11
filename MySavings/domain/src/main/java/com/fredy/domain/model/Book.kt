package com.fredy.domain.model

import android.os.Parcelable
import com.fredy.mysavings.Util.SavingsIcons.bookInitIcon
import kotlinx.parcelize.Parcelize


@Parcelize
data class Book(
    var bookId: String = "",
    val userIdFk: String = "",
    val bookName: String = "DefaultBook",
    val bookIcon: Int = bookInitIcon.image,
    val bookIconDescription: String = bookInitIcon.description,
) : Parcelable {
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


