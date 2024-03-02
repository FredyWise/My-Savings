package com.fredy.mysavings.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Data.APIs.CurrencyModels.Response.Rates
import com.fredy.mysavings.Data.Database.Model.Currency
import com.fredy.mysavings.Data.Repository.CurrencyRepository
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.ViewModels.Event.CurrencyEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepository
) : ViewModel() {

    private val _currencies = currencyRepository.getCurrencies().stateIn(
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

    fun onEvent(event: CurrencyEvent) {
        viewModelScope.launch {
            when (event) {
                is CurrencyEvent.FromCurrency -> {
                    _state.update {
                        it.copy(fromCurrency = event.fromCurrency)
                    }
                }

                is CurrencyEvent.ToCurrency -> {
                    _state.update {
                        it.copy(toCurrency = event.toCurrency)
                    }
                }

                is CurrencyEvent.FromValue -> {
                    _state.update {
                        it.copy(fromValue = event.fromValue)
                    }
                }

                is CurrencyEvent.ToValue -> {
                    _state.update {
                        it.copy(toValue = event.toValue)
                    }
                }

                CurrencyEvent.SaveCurrency -> {
                    _state.value.currency?.let {
                        if (_state.value.updatedValue.toDoubleOrNull().isNotNull()) {
                            val updatedValue = _state.value.updatedValue.toDouble()
                            currencyRepository.updateCurrency(it.copy(value = updatedValue))
                            _state.update { CurrencyState() }
                        }
                    }
                }

                is CurrencyEvent.ShowDialog -> {
                    _state.update {
                        it.copy(currency = event.currency)
                    }
                }

                CurrencyEvent.HideDialog -> {
                    _state.update {
                        it.copy(currency = null)
                    }
                }

                is CurrencyEvent.UpdateValue -> {
                    _state.update {
                        it.copy(updatedValue = event.updatedValue)
                    }
                }
            }
            if (_state.value.fromValue.toDoubleOrNull().isNotNull()) {
                currencyRepository.convertCurrency(
                    _state.value.fromValue.toDouble(),
                    _state.value.fromCurrency,
                    _state.value.toCurrency
                ).collectLatest { result ->
                    when (result) {
                        is Resource.Error -> {}
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            _state.update {
                                it.copy(toValue = result.data!!.amount.toString())
                            }
                        }
                    }
                }
            }
        }
    }

}

data class CurrencyState(
    val ratesResource: Resource<Rates> = Resource.Loading(),
    val currenciesResource: Resource<List<Currency>> = Resource.Loading(),
    val currency: Currency? = null,
    val updatedValue: String = "",
    val fromCurrency: String = "",
    val fromValue: String = "0.0",
    val toCurrency: String = "",
    val toValue: String = "0.0"
)
