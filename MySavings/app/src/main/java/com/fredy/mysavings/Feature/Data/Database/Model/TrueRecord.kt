package com.fredy.mysavings.Feature.Data.Database.Model

import androidx.room.Embedded
import androidx.room.Relation

data class TrueRecord(
    @Embedded val record: Record = Record(),
    @Relation(
        parentColumn = "walletIdFromFk",
        entityColumn = "walletId"
    ) val fromWallet: Wallet = Wallet(),
    @Relation(
        parentColumn = "walletIdToFk",
        entityColumn = "walletId"
    ) val toWallet: Wallet = Wallet(),
    @Relation(
        parentColumn = "categoryIdFk",
        entityColumn = "categoryId"
    ) val toCategory: Category = Category(),
) {
    fun doesMatchSearchQuery(query: String): Boolean {
        return record.doesMatchSearchQuery(query) ||
                fromWallet.doesMatchSearchQuery(query) ||
                toWallet.doesMatchSearchQuery(query) ||
                toCategory.doesMatchSearchQuery(query)
    }

}
