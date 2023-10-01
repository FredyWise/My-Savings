package com.fredy.mysavings.Data.Add

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BtnAction {
    object Save: BtnAction()
    object Cancel: BtnAction()
    data class IncomeSelected(val color: Color): BtnAction()
    data class ExpenseSelected(val color: Color): BtnAction()
    data class TransferSelected(val color: Color): BtnAction()
    object AccountClicked: BtnAction()
    object CategoryClicked: BtnAction()
}

data class Select(
    val income: SelectedState = SelectedState(),
    val expense: SelectedState = SelectedState(),
    var transfer: SelectedState = SelectedState()
)

data class SelectedState(
    val textColor: Color = Color.Gray,
    val icon: ImageVector? = null,
    val iconColor: Color = Color.Gray
)