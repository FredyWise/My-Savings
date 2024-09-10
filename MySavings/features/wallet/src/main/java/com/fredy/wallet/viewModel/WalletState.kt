package com.fredy.wallet.viewModel

import com.fredy.domain.enums.SortType
import com.fredy.domain.model.RecordMap
import com.fredy.domain.model.UserData
import com.fredy.domain.model.Wallet
import com.fredy.domain.util.resource.DataError
import com.fredy.domain.util.resource.Resource
import com.fredy.theme.model.BalanceBar

data class WalletState(
    val currentUser: UserData = UserData(),
    val walletResource: Resource<List<Wallet>, DataError.Local> = Resource.Loading(),
    val recordMapsResource: Resource<List<RecordMap>, DataError.Local> = Resource.Loading(),
    val wallet: Wallet = Wallet(),
    val balanceBar: BalanceBar = BalanceBar(),
    val walletId: String = "",
    val walletName: String = "",
    val walletAmount: String = "",
    val walletCurrency: String = "",
    val walletIcon: Int = 0,
    val walletIconDescription: String = "",
    val isAddingWallet: Boolean = false,
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val sortType: SortType = SortType.ASCENDING
)