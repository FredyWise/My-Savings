package com.fredy.mysavings.Feature.Mappers

import com.fredy.mysavings.Feature.Data.Database.Model.Account

fun List<Account>.getCurrencies():List<String>{
    return this.map { it.accountCurrency }.distinct()
}