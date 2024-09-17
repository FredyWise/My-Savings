package com.fredy.domain.mappers.walletUIMapper

import com.fredy.domain.model.Wallet

fun List<Wallet>.getCurrencies(): List<String> {
    return this.map { it.walletCurrency }.distinct()
}