package com.fredy.mysavings.Data.APIs.CountryModels.Response

data class UsableCurrencyInfoItem(
    val code: String,
    val currencies: CurrencyHelper,
    val flags: Flags
)