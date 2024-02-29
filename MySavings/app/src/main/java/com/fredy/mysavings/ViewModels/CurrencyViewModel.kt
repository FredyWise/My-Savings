package com.fredy.mysavings.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.APIs.CountryModels.Response.Currencies
import com.fredy.mysavings.Data.APIs.CountryModels.Response.CurrencyInfoItem
import com.fredy.mysavings.Data.APIs.CurrencyModels.Response.CurrencyResponse
import com.fredy.mysavings.Data.APIs.CurrencyModels.Response.Rates
import com.fredy.mysavings.Data.Database.Model.Currency
import com.fredy.mysavings.Data.Database.Model.TrueRecord
import com.fredy.mysavings.Data.Repository.CurrencyRepository
import com.fredy.mysavings.Data.Repository.RecordRepository
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepository
): ViewModel() {

    val _currencies = currencyRepository.getCurrencies().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Resource.Success(emptyList())
    )

    private val _state = MutableStateFlow(
        CurrencyState()
    )

    val state = combine(
        _state,
        _currencies,
    ) { state, currencies ->
        state.copy(
            currenciesResource = currencies
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        CurrencyState()
    )



}

data class CurrencyState(
    val ratesResource: Resource<Rates> = Resource.Loading(),
    val currenciesResource: Resource<List<Currency>> = Resource.Loading()
)
