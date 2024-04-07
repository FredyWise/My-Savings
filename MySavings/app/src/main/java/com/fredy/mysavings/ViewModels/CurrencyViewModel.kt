package com.fredy.mysavings.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Data.APIs.CurrencyModels.Response.Rates
import com.fredy.mysavings.Feature.Data.Database.Model.Currency
import com.fredy.mysavings.Feature.Data.Database.Model.UserData
import com.fredy.mysavings.Feature.Mappers.changeBase
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.UserUseCases.UserUseCases
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.ViewModels.Event.CurrencyEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val userUseCases: UserUseCases,
    private val currencyUseCases: CurrencyUseCases
) : ViewModel() {

    init {
        viewModelScope.launch {
            userUseCases.getCurrentUser().collect { userData ->
                userData.data?.let { user ->
                    _state.update {
                        it.copy(
                            userData = user
                        )
                    }
                }
            }
        }
    }

    private val _currenciesResource = currencyUseCases.getCurrencies().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Resource.Success(emptyList())
    )

    private val _state = MutableStateFlow(
        CurrencyState()
    )

    val state = combine(
        _state,
        _currenciesResource,
    ) { state, currenciesResource ->
        val currencies =
            if (currenciesResource is Resource.Success && state.userData.userCurrency != "USD") {
                Resource.Success(currenciesResource.data!!.changeBase(state.userData.userCurrency))
            } else currenciesResource
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

                is CurrencyEvent.BaseCurrency -> {
                    viewModelScope.launch {
                        val user = _state.value.userData.copy(userCurrency = event.baseCurrency)
                        if (_state.value.userData.userCurrency != event.baseCurrency) {
                            userUseCases.updateUser(user)
                        }
                        _state.update {
                            it.copy(userData = user)
                        }
                    }
                }

                CurrencyEvent.SaveCurrency -> {
                    _state.value.currency?.let {
                        if (_state.value.updatedValue.toDoubleOrNull().isNotNull()) {
                            val updatedValue = _state.value.updatedValue.toDouble()
                            currencyUseCases.updateCurrency(it.copy(value = updatedValue))
                            _state.update { it.copy(updatedValue = "") }
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
            if (_state.value.fromValue.toDoubleOrNull()
                    .isNotNull() && _state.value.fromCurrency.isNotEmpty() && _state.value.toCurrency.isNotEmpty()
            ) {
                val result = currencyUseCases.convertCurrencyData(
                    _state.value.fromValue.toDouble(),
                    _state.value.fromCurrency,
                    _state.value.toCurrency
                )

                _state.update {
                    it.copy(toValue = result.amount.toString())
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
    val toValue: String = "0.0",
    val userData: UserData = UserData(),
)
