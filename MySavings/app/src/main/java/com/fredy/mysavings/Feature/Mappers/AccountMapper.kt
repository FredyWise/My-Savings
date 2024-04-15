package com.fredy.mysavings.Feature.Mappers

import com.fredy.mysavings.Feature.Data.Database.Model.Wallet

fun List<Wallet>.getCurrencies():List<String>{
    return this.map { it.walletCurrency }.distinct()
}