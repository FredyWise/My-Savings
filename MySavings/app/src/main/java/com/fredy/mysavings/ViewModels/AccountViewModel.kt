package com.fredy.mysavings.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.Database.Entity.Account
import com.fredy.mysavings.Data.Database.Entity.UserData
import com.fredy.mysavings.Data.Enum.RecordType
import com.fredy.mysavings.Data.Enum.SortType
import com.fredy.mysavings.Data.Repository.AccountRepository
import com.fredy.mysavings.Data.Repository.AuthRepository
import com.fredy.mysavings.Data.Repository.RecordRepository
import com.fredy.mysavings.Data.Repository.TrueRecord
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.TAG
import com.fredy.mysavings.ViewModels.Event.AccountEvent
import com.fredy.mysavings.ViewModels.Event.CategoryEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val authRepository: AuthRepository
): ViewModel() {
    private val _currentUser = MutableStateFlow(
        UserData()
    )
    val currentUser = _currentUser.asStateFlow()
    init {
        viewModelScope.launch {
            authRepository.getCurrentUser()?.let { currentUser ->
                _currentUser.update { currentUser }
            }
        }
    }

    private val _sortType = MutableStateFlow(
        SortType.ASCENDING
    )

    private val _totalAccountBalance: StateFlow<Double?> = accountRepository.getUserAccountTotalBalance().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        0.0
    )

    private val _totalExpense: StateFlow<Double?> = recordRepository.getUserTotalAmountByType(
        RecordType.Expense
    ).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        0.0
    )

    private val _totalIncome: StateFlow<Double?> = recordRepository.getUserTotalAmountByType(
        RecordType.Income
    ).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        0.0
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
            expense = totalExpense ?: 0.0,
            income = totalIncome ?: 0.0,
            balance = totalAccountBalance ?: 0.0,
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
        AccountState(accountCurrency = currentUser.value.userCurrency)
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
        _accounts.value
    )

    val state = combine(
        _state, _sortType, accounts, balanceBar,_records
    ) { state, sortType, accounts, balanceBar, records ->
        state.copy(
            accounts = accounts,
            recordMapsResource = records,
            totalExpense = balanceBar.expense,
            totalIncome = balanceBar.income + balanceBar.balance,
            totalAll = balanceBar.income + balanceBar.expense + balanceBar.balance,
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
                        accountCurrency = currentUser.value.userCurrency,
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
    val accounts: List<Account> = emptyList(),
    val recordMapsResource: Resource<List<RecordMap>> = Resource.Loading(),
    val account: Account = Account(),
    val totalExpense: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalAll: Double = 0.0,
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