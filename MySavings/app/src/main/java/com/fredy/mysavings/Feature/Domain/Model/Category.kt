package com.fredy.mysavings.Feature.Domain.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Presentation.Util.DefaultData.categoryInitIcon

@Entity
data class Category(
    @PrimaryKey
    val categoryId: String = "",
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
            "${categoryType.name}",
        )

        val queries = query.split(",", " ")

        return queries.any { singleQuery ->
            matchingCombinations.any {
                it.contains(singleQuery.trim(), ignoreCase = true)
            }
        }
    }
}




