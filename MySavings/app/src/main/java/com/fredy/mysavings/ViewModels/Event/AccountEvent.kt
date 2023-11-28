package com.fredy.mysavings.ViewModels.Event

import com.fredy.mysavings.Data.Database.Entity.Account
import com.fredy.mysavings.Data.Database.Enum.SortType

sealed interface AccountEvent{
    object SaveAccount: AccountEvent
    data class AccountName(val accountName: String) : AccountEvent
    data class AccountAmount(val amount: String): AccountEvent
    data class AccountCurrency(val currency: String): AccountEvent
    data class AccountIcon(
        val icon: Int,
        val iconDescription: String
    ): AccountEvent

    data class ShowDialog(val account: Account): AccountEvent
    object HideDialog: AccountEvent
    data class SortAccount(val sortType: SortType): AccountEvent
    data class DeleteAccount(val account: Account): AccountEvent
    data class UpdateAccountBalance(val account: Account): AccountEvent
    data class SearchAccount(val name: String): AccountEvent

}
