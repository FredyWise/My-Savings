package com.fredy.data.database.dto

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fredy.data.util.DefaultData
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
) : Parcelable

