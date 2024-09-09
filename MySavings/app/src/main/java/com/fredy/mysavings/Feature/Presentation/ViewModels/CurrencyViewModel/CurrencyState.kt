package com.fredy.mysavings.Feature.Presentation.ViewModels.CurrencyViewModel

import com.fredy.data.api.currencyModels.currencyDTO.Rates
import com.fredy.domain.model.Currency
import com.fredy.domain.model.UserData

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