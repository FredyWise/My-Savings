package com.fredy.mysavings.Data.Database.Entity

import com.fredy.mysavings.Data.Enum.RecordType
import com.fredy.mysavings.R
import com.fredy.mysavings.Util.categoryInitIcon

data class Category(
    var categoryId: String = "",
    val userIdFk: String = "",
    val categoryName: String = "Category",
    val categoryType: RecordType = RecordType.Expense,
    val categoryIcon: Int = categoryInitIcon.image,
    val categoryIconDescription: String = categoryInitIcon.description,
){
    fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            "$categoryName",
            "${categoryName.first()}",
        )

        return matchingCombinations.any {
            it.contains(query, ignoreCase = true)
        }
    }
}




