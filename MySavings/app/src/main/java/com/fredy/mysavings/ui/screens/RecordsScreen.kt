package com.fredy.mysavings.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fredy.mysavings.ViewModels.recordsViewModel
import com.fredy.mysavings.ui.component.Records.CategorizedLazyColumn

@Composable
fun RecordsScreen(
    modifier: Modifier = Modifier,
    recordsViewModel: recordsViewModel = viewModel()
) {
    Column(
        modifier = modifier,

        ) {

        CategorizedLazyColumn(
            records = recordsViewModel.recordsData.records,
            formatDate = {
                recordsViewModel.formatDate(it)
            })
    }
}