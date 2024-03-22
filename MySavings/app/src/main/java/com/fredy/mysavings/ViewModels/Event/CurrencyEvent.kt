package com.fredy.mysavings.ViewModels.Event

import com.fredy.mysavings.Data.Database.Model.Currency

sealed interface CurrencyEvent {
    data class FromCurrency(val fromCurrency: String) : CurrencyEvent
    data class ToCurrency(val toCurrency: String) : CurrencyEvent
    data class BaseCurrency(val baseCurrency: String) : CurrencyEvent
    data class FromValue(val fromValue: String) : CurrencyEvent
    data class ToValue(val toValue: String) : CurrencyEvent
    data class UpdateValue(val updatedValue: String) : CurrencyEvent
    data class ShowDialog(val currency: Currency): CurrencyEvent
    object SaveCurrency: CurrencyEvent
    object HideDialog: CurrencyEvent
}