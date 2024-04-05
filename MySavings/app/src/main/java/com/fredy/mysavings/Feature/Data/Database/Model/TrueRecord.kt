package com.fredy.mysavings.Feature.Data.Database.Model

import androidx.room.Embedded
import androidx.room.Relation
data class TrueRecord(
    @Embedded val record: Record = Record(),
    @Relation(
        parentColumn = "accountIdFromFk",
        entityColumn = "accountId"
    ) val fromAccount: Account = Account(),
    @Relation(
        parentColumn = "accountIdToFk",
        entityColumn = "accountId"
    ) val toAccount: Account = Account(),
    @Relation(
        parentColumn = "categoryIdFk",
        entityColumn = "categoryId"
    ) val toCategory: Category = Category(),
)
