package com.fredy.domain.model

import com.fredy.domain.util.SavingsIcons.walletInitIcon


data class Wallet(
    var walletId: String = "",
    val userIdFk: String = "",
    val walletName: String = "Wallet",
    var walletAmount: Double = 0.0,
    var walletCurrency: String = "",
    val walletIcon: Int = walletInitIcon.image,
    val walletIconDescription: String = walletInitIcon.description,
) {
    fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            "$walletName",
            "${walletName.first()}",
            "$walletCurrency",
        )

        val queries = query.split(",", " ")

        return queries.any { singleQuery ->
            matchingCombinations.any {
                it.contains(singleQuery.trim(), ignoreCase = true)
            }
        }
    }
}

