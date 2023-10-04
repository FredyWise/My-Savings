package com.fredy.mysavings.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.fredy.mysavings.Data.User.Account
import com.fredy.mysavings.Data.Add.BtnAction
import com.fredy.mysavings.Data.Add.MutableTitle
import com.fredy.mysavings.Data.Add.SelectOperation
import com.fredy.mysavings.Data.User.Category
import com.fredy.mysavings.Data.Records.Item

//state.number1 will be used as the saved data in the note
class addViewModel(
//    private val calculatorViewModel: calculatorViewModel,
//    private val dateAndTimeViewModel: dateAndTimeViewModel
): ViewModel() {
    var newItem by mutableStateOf(Item())
    var mutableTitle by mutableStateOf(MutableTitle())

    var textForTextBox by mutableStateOf(newItem.notes)

    fun onTextBoxChange(text: String) {
        textForTextBox = text
    }

    fun onAction(action: BtnAction) {
        when (action) {
            is BtnAction.Save -> performSave()
            is BtnAction.Cancel -> performCancel()
            is BtnAction.Operation -> performSelect(
                action.operation
            )
            is BtnAction.AccountClicked -> showAccountList()
            is BtnAction.ToAccountClicked -> showToAccountList()
            is BtnAction.ToCategoryClicked -> showToCategoryList()
        }

    }

    private fun performSave() {
//        newItem.balance.amount = calculatorViewModel.state.number1.toDouble()
//        newItem.time = dateAndTimeViewModel.pickedDateTime.time
    }

    private fun performCancel() {

    }

    private fun performSelect(selectedOperation: SelectOperation) {
        isTransfer(selectedOperation == SelectOperation.TransferSelected)

    }


    private fun isTransfer(isTransfer: Boolean) {
        if (isTransfer) {
            mutableTitle = mutableTitle.copy(
                fromTitle = "From", toTitle = "To"
            )
            newItem.toCategory = null
            newItem.toAccount = Account()
        } else {
            mutableTitle = MutableTitle()
            newItem.toCategory = Category()
            newItem.toAccount = null
        }
    }

    private fun showAccountList() {

    }

    private fun showToAccountList() {

    }

    private fun showToCategoryList() {

    }

}


