package com.fredy.mysavings.Feature.Domain.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fredy.mysavings.Feature.Data.APIs.ApiCredentials
import com.fredy.mysavings.Feature.Data.APIs.CountryModels.Response.CurrencyInfoItem

@Entity
data class CurrencyInfoCache(
    @PrimaryKey
    val currencyInfo: String = ApiCredentials.CountryModels.CURRENCY_INFO_ID,
    val currencyInfoItems: List<CurrencyInfoItem>
)



