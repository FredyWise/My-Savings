package com.fredy.data.util

import com.fredy.data.enums.RecordType
import com.fredy.domain.model.Book
import com.fredy.domain.model.Category
import com.fredy.domain.model.Wallet

object DefaultData {

    val transferCategory = Category(
        categoryId = "transferCategory",
        userIdFk = "0",
        categoryName = RecordType.Transfer.name,
        categoryType = RecordType.Transfer,
        categoryIcon = transferIcon.image,
        categoryIconDescription = transferIcon.description,
    )

    val deletedCategory = Category(
        categoryId = "deletedCategory",
        userIdFk = "0",
        categoryName = "Deleted Category",
        categoryType = RecordType.Transfer
    )

    val deletedWallet = Wallet(
        walletId = "deletedWallet",
        userIdFk = "0",
        walletName = "Deleted Wallet",
        walletCurrency = "USD"
    )

    val defaultBook = Book()
}