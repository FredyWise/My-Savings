package com.fredy.currency.data.countryModels.countryDTO

data class UsableCurrencyInfoItem(
    val code: String,
    val currencies: CurrencyHelper,
    val flags: Flags
)