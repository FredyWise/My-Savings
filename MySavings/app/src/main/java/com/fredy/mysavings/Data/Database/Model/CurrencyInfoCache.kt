package com.fredy.mysavings.Data.Database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fredy.mysavings.Data.APIs.ApiCredentials
import com.fredy.mysavings.Data.APIs.CountryModels.Response.CurrencyInfoItem
import com.fredy.mysavings.Data.APIs.CountryModels.Response.CurrencyInfoResponse

@Entity
data class CurrencyInfoCache(
    @PrimaryKey
    val currencyInfo: String = ApiCredentials.CountryModels.CURRENCY_INFO_ID,
    val currencyInfoItems: List<CurrencyInfoItem>
)



