package com.fredy.data.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fredy.data.enums.RecordType
import com.fredy.data.util.DefaultData
import com.fredy.mysavings.Feature.Presentation.Util.DefaultData.categoryInitIcon

@Entity
data class Category(
    @PrimaryKey
    val categoryId: String = "",
    val userIdFk: String = "",
    val categoryName: String = "Category",
    val categoryType: RecordType = RecordType.Expense,
    val categoryIcon: Int = DefaultData.categoryInitIcon.image,
    val categoryIconDescription: String = DefaultData.categoryInitIcon.description,
)





