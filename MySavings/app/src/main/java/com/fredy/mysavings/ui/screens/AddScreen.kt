package com.fredy.mysavings.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fredy.mysavings.ViewModels.addViewModel
import com.fredy.mysavings.ui.component.Add.Calculator
import com.fredy.mysavings.ui.theme.SmoothBlue

@Composable
fun AddScreen(
    modifier: Modifier = Modifier,
    addViewModel: addViewModel = viewModel<addViewModel>()
) {
    val state = addViewModel.state
    val defaultSpacing = 10.dp

    Calculator(
        state = state,
        onAction = addViewModel::onAction,
        btnSpacing = defaultSpacing,
        modifier = Modifier
            .fillMaxSize()
            .background(SmoothBlue)
            .padding(16.dp)
    )
}