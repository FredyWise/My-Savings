package com.fredy.mysavings.Feature.Domain.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fredy.mysavings.Feature.Presentation.Util.DefaultData.walletInitIcon

@Entity
data class Wallet(
    @PrimaryKey
    var walletId: String = "",
    val userIdFk: String = "",
    val walletName: String = "Wallet",
    var walletAmount: Double = 0.0,
    var walletCurrency: String = "",
    val walletIcon: Int = walletInitIcon.image,
    val walletIconDescription: String = walletInitIcon.description,
){
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

