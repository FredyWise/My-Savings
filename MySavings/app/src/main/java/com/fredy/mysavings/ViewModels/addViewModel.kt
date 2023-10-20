package com.fredy.mysavings.ViewModels

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.fredy.mysavings.Data.User.Account
import com.fredy.mysavings.Data.Add.BtnAction
import com.fredy.mysavings.Data.Add.MutableTitle
import com.fredy.mysavings.Data.Add.SelectOperation
import com.fredy.mysavings.Data.Add.dateTimeData
import com.fredy.mysavings.Data.User.Category
import com.fredy.mysavings.Data.Records.Item
import com.vanpra.composematerialdialogs.MaterialDialogState
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

//state.number1 will be used as the saved data in the note
class addViewModel(
): ViewModel() {
    var newItem by mutableStateOf(Item())
    var pickedDateTime by mutableStateOf(
        dateTimeData()
    )
    var mutableTitle by mutableStateOf(MutableTitle())

    var textForTextBox by mutableStateOf(newItem.notes)



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

    fun onTextBoxChange(text: String) {
        textForTextBox = text
    }

    private fun performSave() {

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

    fun updateDate(date: LocalDate) {
        pickedDateTime = pickedDateTime.copy(date = date)
    }

    fun updateTime(time: LocalTime) {
        pickedDateTime = pickedDateTime.copy(time = time)
    }

}


