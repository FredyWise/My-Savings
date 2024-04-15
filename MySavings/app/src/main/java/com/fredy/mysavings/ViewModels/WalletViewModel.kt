package com.fredy.mysavings.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Feature.Data.Database.Model.Wallet
import com.fredy.mysavings.Feature.Data.Database.Model.RecordMap
import com.fredy.mysavings.Feature.Data.Database.Model.UserData
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Data.Enum.SortType
import com.fredy.mysavings.Feature.Domain.UseCases.WalletUseCases.WalletUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.UserUseCases.UserUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.RecordUseCases
import com.fredy.mysavings.Util.BalanceBar
import com.fredy.mysavings.Util.BalanceItem
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.ViewModels.Event.WalletEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletUseCases: WalletUseCases,
    private val recordUseCases: RecordUseCases,
    private val userUseCases: UserUseCases,
) : ViewModel() {
    init {
        viewModelScope.launch {
            userUseCases.getCurrentUser().collectLatest { currentUser ->
                when (currentUser) {
                    is Resource.Success -> {
                        currentUser.data?.let { user ->
                            _state.update {
                                WalletState(
                                    currentUser = user,
                                    walletCurrency = user.userCurrency
                                )
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    }


    private val _updating = MutableStateFlow(
        false
    )

    private val _sortType = MutableStateFlow(
        SortType.ASCENDING
    )

    private val _totalAccountBalance = _updating.flatMapLatest {
        walletUseCases.getWalletsTotalBalance()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        BalanceItem()
    )

    private val _totalExpense = _updating.flatMapLatest {
        recordUseCases.getUserTotalAmountByType(
            RecordType.Expense
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        BalanceItem()
    )

    private val _totalIncome = _updating.flatMapLatest {
        recordUseCases.getUserTotalAmountByType(
            RecordType.Income
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        BalanceItem()
    )

    private val _balanceBar = MutableStateFlow(
        BalanceBar()
    )

    private val balanceBar = combine(
        _balanceBar,
        _totalExpense,
        _totalIncome,
        _totalAccountBalance
    ) { balanceBar, totalExpense, totalIncome, totalAccountBalance ->
        balanceBar.copy(
            expense = totalExpense.copy(name = "Total Expense"),
            income = totalIncome.copy(name = "Total Income"),
            balance = totalAccountBalance.copy(name = "Total Balance"),
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        BalanceBar()
    )

    private val _state = MutableStateFlow(
        WalletState()
    )

    private val _accountResource =
        _updating.flatMapLatest { walletUseCases.getWalletsOrderedByName() }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            Resource.Success(emptyList())
        )

    private val _records = _state.flatMapLatest {
        recordUseCases.getUserAccountRecordsOrderedByDateTime(
            it.wallet.walletId,
            _sortType.value
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Resource.Success(emptyList())
    )

    private val accountResource = _state.onEach {
        _state.update {
            it.copy(
                isSearching = true
            )
        }
    }.combine(_accountResource) { state, accountResource ->
        if (state.searchQuery.isBlank()) {
            accountResource
        } else {
            Resource.Success(accountResource.data!!.filter {
                it.doesMatchSearchQuery(state.searchQuery)
            })
        }
    }.onEach {
        _state.update {
            it.copy(
                isSearching = false
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Resource.Success(emptyList())
    )

    val state = combine(
        _state,
        _sortType,
        accountResource,
        balanceBar,
        _records
    ) { state, sortType, accountResource, balanceBar, records ->
        state.copy(
            walletResource = accountResource,
            recordMapsResource = records,
            balanceBar = balanceBar,
            sortType = sortType
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        WalletState()
    )

    fun onEvent(event: WalletEvent) {
        when (event) {
            is WalletEvent.ShowDialog -> {
                _state.update {
                    it.copy(
                        walletId = event.wallet.walletId,
                        walletName = event.wallet.walletName,
                        walletAmount = if (event.wallet.walletAmount != 0.0) event.wallet.walletAmount.toString() else "",
                        walletCurrency = event.wallet.walletCurrency,
                        walletIcon = event.wallet.walletIcon,
                        walletIconDescription = event.wallet.walletIconDescription,
                        isAddingWallet = true,
                    )
                }
            }

            is WalletEvent.HideDialog -> {
                _state.update {
                    it.copy(
                        isAddingWallet = false
                    )
                }
            }

            is WalletEvent.DeleteWallet -> {
                viewModelScope.launch {
                    walletUseCases.deleteWallet(
                        event.wallet
                    )
                    recordUseCases.updateRecordItemWithDeletedAccount(event.wallet)
                    event.onDeleteEffect()
                }
            }

            is WalletEvent.SaveWallet -> {
                val accountId = state.value.walletId
                val accountName = state.value.walletName
                val accountAmount = state.value.walletAmount
                val accountCurrency = state.value.walletCurrency
                val accountIcon = state.value.walletIcon
                val accountIconDescription = state.value.walletIconDescription

                if (accountName.isBlank() || accountAmount.isBlank() || accountCurrency.isBlank() || accountIconDescription.isBlank()) {
                    return
                }

                val wallet = Wallet(
                    walletId = accountId,
                    walletName = accountName,
                    walletAmount = accountAmount.toDouble(),
                    walletCurrency = accountCurrency,
                    walletIcon = accountIcon,
                    walletIconDescription = accountIconDescription,
                )
                viewModelScope.launch {
                    walletUseCases.upsertWallet(
                        wallet
                    )
                }
                _state.update {
                    WalletState(
                        currentUser = it.currentUser,
                        walletCurrency = it.currentUser.userCurrency,
                    )
                }
            }

            is WalletEvent.WalletName -> {
                _state.update {
                    it.copy(
                        walletName = event.accountName
                    )
                }
            }

            is WalletEvent.WalletAmount -> {
                _state.update {
                    it.copy(
                        walletAmount = event.amount
                    )
                }
            }

            is WalletEvent.WalletCurrency -> {
                _state.update {
                    it.copy(
                        walletCurrency = event.currency
                    )
                }
            }

            is WalletEvent.WalletIcon -> {
                _state.update {
                    it.copy(
                        walletIcon = event.icon,
                        walletIconDescription = event.iconDescription
                    )
                }
            }

            is WalletEvent.UpdateWalletBalance -> {
                viewModelScope.launch {
                    walletUseCases.upsertWallet(
                        event.wallet
                    )
                    onEvent(WalletEvent.UpdateWallet)
                }
            }

            is WalletEvent.GetWalletDetail -> {
                _state.update {
                    it.copy(
                        wallet = event.wallet
                    )
                }
            }

            is WalletEvent.SearchWallet -> {
                _state.update {
                    it.copy(
                        searchQuery = event.searchQuery
                    )
                }
            }

            is WalletEvent.SortWallet -> {
                _sortType.value = event.sortType
            }

            is WalletEvent.UpdateWallet -> {
                _updating.update { it.not() }
            }
        }
    }
}

data class WalletState(
    val currentUser: UserData = UserData(),
    val walletResource: Resource<List<Wallet>> = Resource.Loading(),
    val recordMapsResource: Resource<List<RecordMap>> = Resource.Loading(),
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