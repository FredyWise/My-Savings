package com.fredy.mysavings.Feature.Presentation.ViewModels.WalletViewModel

import com.fredy.mysavings.Feature.Domain.Model.Wallet
import com.fredy.mysavings.Feature.Data.Enum.SortType

sealed interface WalletEvent{
    object SaveWallet: WalletEvent
    data class WalletName(val accountName: String) : WalletEvent
    data class WalletAmount(val amount: String): WalletEvent
    data class WalletCurrency(val currency: String): WalletEvent
    data class WalletIcon(
        val icon: Int,
        val iconDescription: String
    ): WalletEvent

    data class ShowDialog(val wallet: Wallet): WalletEvent
    object HideDialog: WalletEvent
    data class SortWallet(val sortType: SortType): WalletEvent
    data class DeleteWallet(val wallet: Wallet, val onDeleteEffect: ()->Unit): WalletEvent
    data class UpdateWalletBalance(val wallet: Wallet): WalletEvent
    data class SearchWallet(val searchQuery: String): WalletEvent
    data class GetWalletDetail(val wallet: Wallet): WalletEvent
    object UpdateWallet: WalletEvent

}
