package com.fredy.mysavings.Feature.Data.Database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fredy.mysavings.Util.DefaultData

@Entity
data class Book(
    @PrimaryKey
    var bookId: String = "",
    val userIdFk: String = "",
    val bookName: String = "DefaultBook",
    val bookIcon: Int = DefaultData.bookInitIcon.image,
    val bookIconDescription: String = DefaultData.bookInitIcon.description,
)