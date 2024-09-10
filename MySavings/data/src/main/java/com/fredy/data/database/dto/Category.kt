package com.fredy.data.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fredy.domain.enums.RecordType
import com.fredy.domain.util.DefaultData
import com.fredy.theme.util.SavingsIcons.categoryInitIcon

@Entity
data class Category(
    @PrimaryKey
    val categoryId: String = "",
    val userIdFk: String = "",
    val categoryName: String = "Category",
    val categoryType: RecordType = RecordType.Expense,
    val categoryIcon: Int = categoryInitIcon.image,
    val categoryIconDescription: String = categoryInitIcon.description,
)





