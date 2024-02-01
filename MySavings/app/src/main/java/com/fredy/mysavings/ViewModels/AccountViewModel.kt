package com.fredy.mysavings.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.Database.Model.Account
import com.fredy.mysavings.Data.Database.Model.UserData
import com.fredy.mysavings.Data.Enum.RecordType
import com.fredy.mysavings.Data.Enum.SortType
import com.fredy.mysavings.Data.Repository.AccountRepository
import com.fredy.mysavings.Data.Repository.RecordRepository
import com.fredy.mysavings.Data.Repository.UserRepository
import com.fredy.mysavings.Util.BalanceBar
import com.fredy.mysavings.Util.BalanceItem
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.ViewModels.Event.AccountEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val recordRepository: RecordRepository,
    private val userRepository: UserRepository,
): ViewModel() {
    init {
        viewModelScope.launch {
            userRepository.getCurrentUser().collectLatest { currentUser ->
                currentUser?.let {
                    _state.update {
                        AccountState(
                            currentUser = currentUser,
                            accountCurrency = currentUser.userCurrency
                        )
                    }
                }
            }
        }
    }

    private val _sortType = MutableStateFlow(
        SortType.ASCENDING
    )

    private val _totalAccountBalance = accountRepository.getUserAccountTotalBalance().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        BalanceItem()
    )

    private val _totalExpense = recordRepository.getUserTotalAmountByType(
        RecordType.Expense
    ).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        BalanceItem()
    )

    private val _totalIncome = recordRepository.getUserTotalAmountByType(
        RecordType.Income
    ).stateIn(
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
            expense = totalExpense.copy(name = "Expense So Far"),
            income = totalIncome.copy(name = "Income So Far"),
            balance = totalAccountBalance.copy(name = "Total Balance"),
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        BalanceBar()
    )

    private val _accounts = accountRepository.getUserAccountOrderedByName().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    private val _state = MutableStateFlow(
        AccountState()
    )

    private val _records = _state.flatMapLatest {
        recordRepository.getUserAccountRecordsOrderedByDateTime(
            it.account.accountId, _sortType.value
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Resource.Success(emptyList())
    )

    private val accounts = _state.onEach {
        _state.update {
            it.copy(
                isSearching = true
            )
        }
    }.combine(_accounts) { state, accounts ->
        if (state.searchText.isBlank()) {
            accounts
        } else {
            accounts.filter {
                it.doesMatchSearchQuery(state.searchText)
            }
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
        emptyList()
    )

    val state = combine(
        _state,
        _sortType,
        accounts,
        balanceBar,
        _records
    ) { state, sortType, accounts, balanceBar, records ->
        state.copy(
            accounts = accounts,
            recordMapsResource = records,
            balanceBar = balanceBar,
            sortType = sortType
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        AccountState()
    )

    fun onEvent(event: AccountEvent) {
        when (event) {
            is AccountEvent.ShowDialog -> {
                _state.update {
                    it.copy(
                        accountId = event.account.accountId,
                        accountName = event.account.accountName,
                        accountAmount = if (event.account.accountAmount != 0.0) event.account.accountAmount.toString() else "",
                        accountCurrency = event.account.accountCurrency,
                        accountIcon = event.account.accountIcon,
                        accountIconDescription = event.account.accountIconDescription,
                        isAddingAccount = true,
                    )
                }
            }

            is AccountEvent.HideDialog -> {
                _state.update {
                    it.copy(
                        isAddingAccount = false
                    )
                }
            }

            is AccountEvent.DeleteAccount -> {
                viewModelScope.launch {
                    accountRepository.deleteAccount(
                        event.account
                    )
                }
            }

            is AccountEvent.SaveAccount -> {
                val accountId = state.value.accountId
                val accountName = state.value.accountName
                val accountAmount = state.value.accountAmount
                val accountCurrency = state.value.accountCurrency
                val accountIcon = state.value.accountIcon
                val accountIconDescription = state.value.accountIconDescription

                if (accountName.isBlank() || accountAmount.isBlank() || accountCurrency.isBlank() || accountIconDescription.isBlank()) {
                    return
                }

                val account = Account(
                    accountId = accountId,
                    accountName = accountName,
                    accountAmount = accountAmount.toDouble(),
                    accountCurrency = accountCurrency,
                    accountIcon = accountIcon,
                    accountIconDescription = accountIconDescription,
                )
                viewModelScope.launch {
                    accountRepository.upsertAccount(
                        account
                    )
                }
                _state.update {
                    AccountState(
                        currentUser = it.currentUser,
                        accountCurrency = it.currentUser.userCurrency,
                    )
                }
            }

            is AccountEvent.AccountName -> {
                _state.update {
                    it.copy(
                        accountName = event.accountName
                    )
                }
            }

            is AccountEvent.AccountAmount -> {
                _state.update {
                    it.copy(
                        accountAmount = event.amount
                    )
                }
            }

            is AccountEvent.AccountCurrency -> {
                _state.update {
                    it.copy(
                        accountCurrency = event.currency
                    )
                }
            }

            is AccountEvent.AccountIcon -> {
                _state.update {
                    it.copy(
                        accountIcon = event.icon,
                        accountIconDescription = event.iconDescription
                    )
                }
            }

            is AccountEvent.UpdateAccountBalance -> {
                viewModelScope.launch {
                    accountRepository.upsertAccount(
                        event.account
                    )
                }
            }

            is AccountEvent.GetAccountDetail -> {
                _state.update {
                    it.copy(
                        account = event.account
                    )
                }
            }

            is AccountEvent.SearchAccount -> {
                _state.update {
                    it.copy(
                        searchText = event.name
                    )
                }
            }

            is AccountEvent.SortAccount -> {
                _sortType.value = event.sortType
            }

        }
    }
}

data class AccountState(
    val currentUser: UserData = UserData(),
    val accounts: List<Account> = emptyList(),
    val recordMapsResource: Resource<List<RecordMap>> = Resource.Loading(),
    val account: Account = Account(),
    val balanceBar: BalanceBar = BalanceBar(),
    val accountId: String = "",
    val accountName: String = "",
    val accountAmount: String = "",
    val accountCurrency: String = "",
    val accountIcon: Int = 0,
    val accountIconDescription: String = "",
    val isAddingAccount: Boolean = false,
    val searchText: String = "",
    val isSearching: Boolean = false,
    val sortType: SortType = SortType.ASCENDING
)