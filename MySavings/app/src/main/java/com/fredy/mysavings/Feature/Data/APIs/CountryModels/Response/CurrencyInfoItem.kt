package com.fredy.mysavings.Feature.Data.APIs.CountryModels.Response

import com.fredy.data.api.countryModels.countryDTO.Currencies
import com.fredy.data.api.countryModels.countryDTO.Flags

data class CurrencyInfoItem(
    val currencies: Currencies,
    val flags: Flags
)