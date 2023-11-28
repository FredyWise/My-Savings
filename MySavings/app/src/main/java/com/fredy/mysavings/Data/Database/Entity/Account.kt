package com.fredy.mysavings.Data.Database.Entity

import com.fredy.mysavings.R

data class Account(
    var accountId: String = "",
    val userIdFk: String = "",
    val accountName: String = "Account",
    var accountAmount: Double = 0.0,
    var accountCurrency: String = "",
    val accountIcon: Int = R.drawable.ic_wallet_foreground,
    val accountIconDescription: String = "",
){
    fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            "$accountName",
            "${accountName.first()}",
        )

        return matchingCombinations.any {
            it.contains(query, ignoreCase = true)
        }
    }
}

