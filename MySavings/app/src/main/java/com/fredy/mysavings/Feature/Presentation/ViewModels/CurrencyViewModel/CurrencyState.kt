package com.fredy.mysavings.Feature.Presentation.ViewModels.CurrencyViewModel

import com.fredy.mysavings.Feature.Data.APIs.CurrencyModels.Response.Rates
import com.fredy.mysavings.Feature.Domain.Model.Currency
import com.fredy.mysavings.Feature.Domain.Model.UserData
import com.fredy.mysavings.Feature.Domain.Util.Resource

data class CurrencyState(
    val ratesResource: Resource<Rates> = Resource.Loading(),
    val currenciesResource: Resource<List<Currency>> = Resource.Loading(),
    val currency: Currency? = null,
    val updatedValue: String = "",
    val fromCurrency: String = "",
    val fromValue: String = "0.0",
    val toCurrency: String = "",
    val toValue: String = "0.0",
    val userData: UserData = UserData(),
)