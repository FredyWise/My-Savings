package com.fredy.domain.model

import androidx.room.Embedded
import androidx.room.Relation
import com.fredy.ui.util.formatDateDay

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
        val matchingCombinations = listOf(
            "${com.fredy.ui.util.formatDateDay(record.recordDateTime)}",
            "${record.recordAmount}",
            "${record.recordType.name}",
            "${record.recordNotes}",
            "${fromWallet.walletName}",
            "${fromWallet.walletCurrency}",
            "${toWallet.walletName}",
            "${toWallet.walletCurrency}",
            "${toCategory.categoryName}",
        )
        val queries = query.split(",", " ")

        return queries.any { singleQuery ->
            matchingCombinations.any {
                it.contains(singleQuery.trim(), ignoreCase = true)
            }
        }
    }

    fun doesMatchAllSearchQuery(query: String): Boolean {
        return record.doesMatchSearchQuery(query) ||
                fromWallet.doesMatchSearchQuery(query) ||
                toWallet.doesMatchSearchQuery(query) ||
                toCategory.doesMatchSearchQuery(query)
    }
}
