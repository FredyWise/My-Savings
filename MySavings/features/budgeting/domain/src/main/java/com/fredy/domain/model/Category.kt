package com.fredy.domain.model

import com.fredy.domain.enums.RecordType
import com.fredy.domain.util.SavingsIcons.categoryInitIcon


data class Category(
    val categoryId: String = "",
    val userIdFk: String = "",
    val categoryName: String = "Category",
    val categoryType: RecordType = RecordType.Expense,
    val categoryIcon: Int = categoryInitIcon.image,
    val categoryIconDescription: String = categoryInitIcon.description,
)




