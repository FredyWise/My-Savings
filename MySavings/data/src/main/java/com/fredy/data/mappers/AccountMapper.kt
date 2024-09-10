package com.fredy.data.mappers

import com.fredy.domain.model.Wallet

fun List<Wallet>.getCurrencies():List<String>{
    return this.map { it.walletCurrency }.distinct()
}