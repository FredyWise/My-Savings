package com.fredy.currency.viewModel

import com.fredy.domain.model.Currency
import com.fredy.domain.model.Rate
import com.fredy.domain.model.UserData
import com.fredy.core.util.resource.DataError
import com.fredy.core.util.resource.Resource

data class CurrencyState(
    val ratesResource: Resource<List<Rate>, DataError.Local> = Resource.Loading(),
    val currenciesResource: Resource<List<Currency>, DataError.Local> = Resource.Loading(),
    val currency: Currency? = null,
    val updatedValue: String = "",
    val fromCurrency: String = "",
    val fromValue: String = "0.0",
    val toCurrency: String = "",
    val toValue: String = "0.0",
    val userData: UserData = UserData(),
)