package com.fredy.data.database.dto

import com.fredy.data.api.countryModels.countryDTO.CurrencyHelper
import com.fredy.data.api.countryModels.countryDTO.Flags

data class UsableCurrencyInfoItem(
    val code: String,
    val currencies: CurrencyHelper,
    val flags: Flags
)