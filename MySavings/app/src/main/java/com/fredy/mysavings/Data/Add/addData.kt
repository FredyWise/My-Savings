package com.fredy.mysavings.Data.Add

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.graphics.vector.ImageVector



sealed class BtnAction {
    object Save: BtnAction()
    object Cancel: BtnAction()
    data class Operation(val operation: SelectOperation): BtnAction()
    object AccountClicked: BtnAction()
    object ToAccountClicked: BtnAction()
    object ToCategoryClicked: BtnAction()
}

val selectOperations = listOf(
    SelectOperation.IncomeSelected,
    SelectOperation.ExpenseSelected,
    SelectOperation.TransferSelected,
)
sealed class SelectOperation(val selectedState: SelectedState) {
    object IncomeSelected: SelectOperation(
        SelectedState(text = "INCOME")
    )
    object ExpenseSelected: SelectOperation(SelectedState(text = "EXPENSE"))
    object TransferSelected: SelectOperation(SelectedState(text = "TRANSFER"))
}


data class SelectedState(
    val text: String,
    val icon: ImageVector = Icons.Default.CheckCircle,
)

data class MutableTitle(
    var fromTitle: String = "Account",
    var toTitle: String = "Category"
)