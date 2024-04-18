package com.fredy.mysavings.Util.Mappers

import com.fredy.mysavings.Feature.Domain.Model.Wallet

fun List<Wallet>.getCurrencies():List<String>{
    return this.map { it.walletCurrency }.distinct()
}