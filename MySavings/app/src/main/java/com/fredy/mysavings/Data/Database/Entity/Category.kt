package com.fredy.mysavings.Data.Database.Entity

import com.fredy.mysavings.Data.Enum.RecordType
import com.fredy.mysavings.R

data class Category(
    var categoryId: String = "",
    val userIdFk: String = "",
    val categoryName: String = "Category",
    val categoryType: RecordType = RecordType.Expense,
    val categoryIcon: Int = R.drawable.ic_category_foreground,
    val categoryIconDescription: String = "",
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




