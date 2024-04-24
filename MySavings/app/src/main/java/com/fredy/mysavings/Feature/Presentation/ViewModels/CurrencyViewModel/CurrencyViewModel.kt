package com.fredy.mysavings.Feature.Presentation.ViewModels.CurrencyViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Data.APIs.ApiCredentials
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.UserUseCases.UserUseCases
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.fredy.mysavings.Util.Log
import com.fredy.mysavings.Feature.Domain.Util.Mappers.changeBase
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
                    Log.i("$user")
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
                    _state.value.currency?.let { currency ->
                        if (_state.value.updatedValue.toDoubleOrNull().isNotNull()) {
                            val value = _state.value.updatedValue.toDouble()
                            val userCurrency = _state.value.userData.userCurrency
                            val updatedValue = if (userCurrency == ApiCredentials.CurrencyModels.BASE_CURRENCY) {
                                value
                            } else {
                                currencyUseCases.convertCurrencyData(
                                    1.0,
                                    ApiCredentials.CurrencyModels.BASE_CURRENCY,
                                    userCurrency
                                ).amount*value
                            }
                            currencyUseCases.updateCurrency(currency.copy(value = updatedValue))
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

