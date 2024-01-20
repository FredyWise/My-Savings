package com.fredy.mysavings.ui.Screens.Other

import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.fredy.mysavings.ViewModels.CurrencyViewModel
import com.fredy.mysavings.ui.Screens.ZCommonComponent.CurrencyDropdown
import com.fredy.mysavings.ui.Screens.ZCommonComponent.DefaultAppBar

@Composable
fun PreferencesScreen(
    modifier: Modifier = Modifier,
    rootNavController: NavController,
    title: String,
    viewModel: CurrencyViewModel = hiltViewModel()
) {
    var amount by remember { mutableStateOf(0.0) }
    var resource = viewModel.resource
    DefaultAppBar(
        modifier = modifier,
        title = title,
        onNavigationIconClick = { rootNavController.navigateUp() },
    ) {
        OutlinedTextField(value = amount.toString(),
            onValueChange = { amount = it.toDouble() })
        CurrencyDropdown(selectedText = "",
            onClick = {
                viewModel.convertFrom(
                    amount.toString(),
                    it
                )
            })
        resource.value.success?.let {
            Text(text = it)
        }
    }
}
