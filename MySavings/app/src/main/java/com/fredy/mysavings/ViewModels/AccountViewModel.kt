package com.fredy.mysavings.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.RoomDatabase.Entity.Account
import com.fredy.mysavings.Data.RoomDatabase.Enum.RecordType
import com.fredy.mysavings.Data.RoomDatabase.Enum.SortType
import com.fredy.mysavings.Data.RoomDatabase.Event.AccountEvent
import com.fredy.mysavings.ui.Repository.AccountRepositoryImpl
import com.fredy.mysavings.ui.Repository.Graph
import com.fredy.mysavings.ui.Repository.RecordRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountViewModel(
    private val accountRepository: AccountRepositoryImpl = Graph.accountRepository,
    private val recordRepository: RecordRepositoryImpl = Graph.recordRepository,
): ViewModel() {
    private val _sortType = MutableStateFlow(
        SortType.ASCENDING
    )

    private val _accounts = accountRepository.getUserAccountOrderedByName().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    private val _state = MutableStateFlow(
        AccountState()
    )

    init {
        viewModelScope.launch {
            recordRepository.getUserTotalAmountByType(
                RecordType.Expense
            ).collectLatest { totalExpense ->
                _state.update {
                    it.copy(
                        totalExpense = totalExpense,
                        totalAll = totalExpense + it.totalIncome + it.totalAccountBalance
                    )
                }
            }
        }
        viewModelScope.launch {
            recordRepository.getUserTotalAmountByType(
                RecordType.Income
            ).collectLatest { totalIncome ->
                _state.update {
                    it.copy(
                        totalIncome = totalIncome+ it.totalAccountBalance,
                        tempIncome = totalIncome,
                        totalAll = totalIncome + it.totalExpense + it.totalAccountBalance
                    )
                }
            }
        }
        viewModelScope.launch {
            accountRepository.getUserAccountTotalBalance().collectLatest { totalBalance ->
                _state.update {
                    it.copy(
                        totalAccountBalance = totalBalance,
                        totalIncome = totalBalance + it.tempIncome,
                        totalAll = totalBalance + it.tempIncome + it.totalExpense
                    )
                }
            }
        }
    }


    val state = combine(
        _state, _sortType, _accounts,
    ) { state, sortType, accounts ->
        state.copy(
            accounts = accounts,
            sortType = sortType
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
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
                val accountCurrency = "hello"//state.value.accountCurrency
                val accountIcon = state.value.accountIcon
                val accountIconDescription = state.value.accountIconDescription

                if (accountName.isBlank() || accountAmount == "" || accountCurrency.isBlank() || accountIcon == 0 || accountIconDescription.isBlank()) {
                    return
                }

                val account = Account(
                    accountId = accountId,
                    accountName = accountName,
                    accountAmount = accountAmount.toDouble(),
                    accountCurrency = accountCurrency,
                    accountIconDescription = accountIconDescription,
                    accountIcon = accountIcon,
                )
                viewModelScope.launch {
                    accountRepository.upsertAccount(
                        account
                    )
                }
                _state.update {
                    it.copy(
                        accountId = 0,
                        accountName = "",
                        accountAmount = "",
                        accountCurrency = "",
                        accountIcon = 0,
                        accountIconDescription = "",
                        isAddingAccount = false
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

            is AccountEvent.SortAccount -> {
                _sortType.value = event.sortType
            }
        }
    }
}

data class AccountState(
    val accounts: List<Account> = emptyList(),
    val totalExpense: Double = 0.0,
    val tempIncome: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalAccountBalance: Double = 0.0,
    val totalAll: Double = 0.0,
    val accountId: Int = 0,
    val accountName: String = "",
    val accountAmount: String = "",
    val accountCurrency: String = "",
    val accountIcon: Int = 0,
    val accountIconDescription: String = "",
    val isAddingAccount: Boolean = false,
    val sortType: SortType = SortType.ASCENDING
)