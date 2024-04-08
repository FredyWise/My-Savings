package com.fredy.mysavings.Feature.Data.Database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fredy.mysavings.Util.DefaultData.accountInitIcon

@Entity
data class Account(
    @PrimaryKey
    var accountId: String = "",
    val userIdFk: String = "",
    val accountName: String = "Account",
    var accountAmount: Double = 0.0,
    var accountCurrency: String = "",
    val accountIcon: Int = accountInitIcon.image,
    val accountIconDescription: String = accountInitIcon.description,
){
    fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            "$accountName",
            "${accountName.first()}",
            "$accountCurrency",
        )

        return matchingCombinations.any {
            it.contains(query, ignoreCase = true)
        }
    }
}

