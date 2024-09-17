package com.fredy.data.mappers

import com.fredy.data.database.dto.Wallet as DataWallet
import com.fredy.domain.model.Wallet as DomainWallet


fun List<DataWallet>.toDomainWallet(): List<DomainWallet> {
    return this.map { it.toDomainWallet() }
}

fun List<DomainWallet>.toDataWallet(): List<DataWallet> {
    return this.map { it.toDataWallet() }
}

fun DataWallet.toDomainWallet(): DomainWallet {
    return DomainWallet(
        walletId,
        userIdFk,
        walletName,
        walletAmount,
        walletCurrency,
        walletIcon,
        walletIconDescription,
    )
}

fun DomainWallet.toDataWallet(): DataWallet {
    return DataWallet(
        walletId,
        userIdFk,
        walletName,
        walletAmount,
        walletCurrency,
        walletIcon,
        walletIconDescription,
    )
}