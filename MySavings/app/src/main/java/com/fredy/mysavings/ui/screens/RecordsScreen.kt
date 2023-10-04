package com.fredy.mysavings.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fredy.mysavings.Data.User.UserData
import com.fredy.mysavings.Data.balanceBars
import com.fredy.mysavings.ViewModels.recordsViewModel
import com.fredy.mysavings.ui.component.Records.BalanceBar
import com.fredy.mysavings.ui.component.Records.CategorizedLazyColumn

@Composable
fun RecordsScreen(
    modifier: Modifier = Modifier,
    recordsViewModel: recordsViewModel = viewModel()
) {
    Column(
        modifier = modifier,
        ) {
        BalanceBar(
            modifier = Modifier.background(
                MaterialTheme.colorScheme.surface
            ),
            balanceBars = balanceBars
        )
        CategorizedLazyColumn(
            records = recordsViewModel.recordsData.records,
            formatDate = recordsViewModel::formatDate
        )
    }
}