package com.fredy.mysavings.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.RoomDatabase.Entity.Account
import com.fredy.mysavings.Data.RoomDatabase.Enum.SortType
import com.fredy.mysavings.Data.RoomDatabase.Event.AccountEvent
import com.fredy.mysavings.R
import com.fredy.mysavings.ui.Repository.AccountRepositoryImpl
import com.fredy.mysavings.ui.Repository.Graph
import com.fredy.mysavings.ui.Repository.RecordRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
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

    private val _totalExpense = recordRepository.getUserTotalExpenses().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        -0.0
    )

    private val _totalIncome = recordRepository.getUserTotalIncomes().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        0.0
    )

    private val _totalBalance = recordRepository.getUserTotalBalance().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    private val _accounts = accountRepository.getUserAccountOrderedByName().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    private val _state = MutableStateFlow(
        AccountState()
    )


    val state = combine(
        _state, _sortType, _accounts,
    ) { state, sortType, accounts ->
        state.copy(
            totalExpense = _totalExpense.value,
            totalIncome = _totalIncome.value,
//            totalAll = _totalBalance.value,
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
//
//            is AccountEvent.ShowOption -> {
//                _state.update {
//                    it.copy(
//                        isShowMenu = true,
//                        pressOffset = DpOffset(it.pressOffset.x, it.pressOffset.y)
//                    )
//                }
//            }
//
//            is AccountEvent.HideOption -> {
//                _state.update {
//                    it.copy(
//                        isShowMenu = false,
//                        pressOffset = DpOffset.Zero
//                    )
//                }
//            }

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

                if (accountName.isBlank() || accountAmount == "" || accountCurrency.isBlank() || accountIcon == 0 || accountIconDescription.isBlank()) {
                    return
                }

                val account = Account(
                    accountId = accountId!!,
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
                        accountId = null,
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

            is AccountEvent.Dummy -> {
                val account = Account(
                    accountName = "babi",
                    accountAmount = 10.0,
                    accountCurrency = "USD",
                    accountIcon = R.drawable.ic_launcher_background,
                    accountIconDescription = "bodoh",
                )
                Log.e("BABI", "onTB: "+_totalBalance.value)
                viewModelScope.launch {
                    Log.e(
                        "BABI",
                        "onEvent: " + state.value + _accounts.value
                    )
                    accountRepository.upsertAccount(
                        account
                    )
                }
            }
        }

//        private fun filterBy(shoppingListId:Int){
//            if (shoppingListId != 10001){
//                viewModelScope.launch {
//                    repository.getItemWithStoreAndListFilteredById(
//                        shoppingListId
//                    ).collectLatest {
//                        state = state.copy(items = it)
//                    }
//                }
//            }else{
//                getItems()
//            }
//        }
    }
}

data class AccountState(
    val accounts: List<Account> = emptyList(),
    val totalExpense: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalAll: Double = 0.0,
    val accountId: Int? = null,
    val accountName: String = "",
    val accountAmount: String = "",
    val accountCurrency: String = "",
    val accountIcon: Int = 0,
    val accountIconDescription: String = "",
    val isAddingAccount: Boolean = false,
    val sortType: SortType = SortType.ASCENDING
)