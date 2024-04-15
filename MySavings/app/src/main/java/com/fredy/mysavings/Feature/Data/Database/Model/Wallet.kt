package com.fredy.mysavings.Feature.Data.Database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fredy.mysavings.Util.DefaultData.walletInitIcon

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

        return matchingCombinations.any {
            it.contains(query, ignoreCase = true)
        }
    }
}

