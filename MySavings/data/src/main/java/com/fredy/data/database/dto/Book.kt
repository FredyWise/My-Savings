package com.fredy.data.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fredy.mysavings.Util.SavingsIcons.bookInitIcon

@Entity
data class Book(
    @PrimaryKey
    var bookId: String = "",
    val userIdFk: String = "",
    val bookName: String = "DefaultBook",
    val bookIcon: Int = bookInitIcon.image,
    val bookIconDescription: String = bookInitIcon.description,
)

