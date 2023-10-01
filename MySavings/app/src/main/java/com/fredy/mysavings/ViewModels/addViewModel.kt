package com.fredy.mysavings.ViewModels

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.fredy.mysavings.Data.Add.BtnAction
import com.fredy.mysavings.Data.Add.Select
import com.fredy.mysavings.Data.Add.SelectedState
import java.time.format.DateTimeFormatter

//state.number1 will be used as the saved data in the note
class addViewModel: ViewModel() {
    var select by mutableStateOf(Select())
    var textForTextBox by mutableStateOf(
        ""
    )

    fun onTextBoxChange(text: String) {
        textForTextBox = text
    }

    fun onAction(action: BtnAction) {
        when (action) {
            is BtnAction.Save -> performSave()
            is BtnAction.Cancel -> performCancel()
            is BtnAction.IncomeSelected -> selectIncome(action.color)
            is BtnAction.ExpenseSelected -> selectExpense(action.color)
            is BtnAction.TransferSelected -> selectTransfer(action.color)
            is BtnAction.AccountClicked -> showAccountList()
            is BtnAction.CategoryClicked -> showCategoryList()
        }

    }

    private fun performSave() {

    }

    private fun performCancel() {
        select = Select()

    }

    private fun selectIncome(color: Color) {
        select = Select()
        select = select.copy(
            income = SelectedState(
                textColor = color,
                icon = Icons.Default.CheckCircle,
                iconColor = color
            )
        )
    }

    private fun selectExpense(color: Color) {
        select = Select()
        select = select.copy(
            expense = SelectedState(
                textColor = color,
                icon = Icons.Default.CheckCircle,
                iconColor = color
            )
        )
    }

    private fun selectTransfer(color: Color) {
        select = Select()
        select = select.copy(
            transfer = SelectedState(
                textColor = color,
                icon = Icons.Default.CheckCircle,
                iconColor = color
            )
        )
    }

    private fun showAccountList() {

    }

    private fun showCategoryList() {

    }

}


