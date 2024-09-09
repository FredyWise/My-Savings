package com.fredy.mysavings.Feature.Data.APIs.CountryModels.Response.AdditionalData

import com.fredy.data.api.countryModels.countryDTO.additionalDTO.NativeName

data class Name(
    val common: String,
    val nativeName: NativeName,
    val official: String
)