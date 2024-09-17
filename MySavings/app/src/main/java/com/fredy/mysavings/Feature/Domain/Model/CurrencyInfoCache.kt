package com.fredy.mysavings.Feature.Domain.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fredy.domain.credentials.ApiCredentials
import com.fredy.data.api.countryModels.countryDTO.CurrencyInfoItem

@Entity
data class CurrencyInfoCache(
    @PrimaryKey
    val currencyInfo: String = ApiCredentials.CountryModels.CURRENCY_INFO_ID,
    val currencyInfoItems: List<CurrencyInfoItem>
)



