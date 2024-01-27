package com.fredy.mysavings.ViewModels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.APIs.CurrencyModels.Response.Rates
import com.fredy.mysavings.Data.Database.Model.UserData
import com.fredy.mysavings.Data.Repository.CurrencyRepository
import com.fredy.mysavings.Util.BalanceItem
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.ResourceState
import com.fredy.mysavings.Util.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val repository: CurrencyRepository,
    private val currentUserData: UserData?,
): ViewModel() {

    private val _resource = mutableStateOf(
        ResourceState()
    )
    val resource: State<ResourceState> = _resource

    fun convertTo(
        amountStr: String,
        toCurrencyTemp: String
    ) {
        Log.e(TAG, "convert: "+amountStr+toCurrencyTemp )
        val fromAmount = amountStr.toFloatOrNull()
        val toCurrency = currentUserData!!.userCurrency.ifBlank { "USD" }
        val fromCurrency =  toCurrencyTemp

        if (fromAmount == null) {
            _resource.value = ResourceState(error = "Amount is Empty")
            return
        }

        viewModelScope.launch {
            repository.convertCurrency(
                amountStr.toDouble(),
                fromCurrency,
                toCurrency
            ).collect { result ->
                when (result) {
                    is Resource.Error -> _resource.value = ResourceState(
                        error = result.message!!
                    )

                    is Resource.Success -> {
                        val balanceItem = result.data!!
                        Log.e(
                            TAG,
                            "convertTo: success"+ balanceItem,

                        )
                        _resource.value = ResourceState(
                            success = "${balanceItem}"
                        )
                    }

                    is Resource.Loading -> _resource.value = ResourceState(
                        isLoading = true
                    )
                }
            }
        }
    }

}



