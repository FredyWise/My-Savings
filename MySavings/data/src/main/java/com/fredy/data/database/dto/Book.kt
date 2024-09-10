package com.fredy.data.database.dto

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fredy.domain.util.DefaultData
import com.fredy.theme.util.SavingsIcons.bookInitIcon
import kotlinx.parcelize.Parcelize

@Entity
data class Book(
    @PrimaryKey
    var bookId: String = "",
    val userIdFk: String = "",
    val bookName: String = "DefaultBook",
    val bookIcon: Int = bookInitIcon.image,
    val bookIconDescription: String = bookInitIcon.description,
)

