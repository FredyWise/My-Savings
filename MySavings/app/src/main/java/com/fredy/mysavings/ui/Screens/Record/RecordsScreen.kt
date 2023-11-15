package com.fredy.mysavings.ui.Screens.Record

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fredy.mysavings.Data.RoomDatabase.Enum.SortType
import com.fredy.mysavings.Data.RoomDatabase.Event.RecordsEvent
import com.fredy.mysavings.Data.balanceBars
import com.fredy.mysavings.ViewModel.RecordViewModel
import com.fredy.mysavings.ui.Navigation.NavigationRoute
import com.fredy.mysavings.ui.Navigation.navigateSingleTopTo


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RecordsScreen(
    modifier: Modifier = Modifier,
    rootNavController: NavHostController,
    viewModel: RecordViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    Column(
        modifier = modifier,
    ) {
        state.trueRecord?.let {
            RecordDialog(
                trueRecord = it,
                onEvent = viewModel::onEvent,
                onEdit = {
                    rootNavController.navigateSingleTopTo(
                        NavigationRoute.Add.route+"?id=-1"
                    )
                }
            )
        }
        BalanceBar(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.surface
                )
                .padding(vertical = 5.dp),
            amountBars = balanceBars
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(
                    rememberScrollState()
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SortType.values().forEach { sortType ->
                Row(
                    modifier = Modifier.clickable {
                        viewModel.onEvent(
                            RecordsEvent.SortRecord(
                                sortType
                            )
                        )
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = state.sortType == sortType,
                        onClick = {
                            viewModel.onEvent(
                                RecordsEvent.SortRecord(
                                    sortType
                                )
                            )
                        })
                    Text(text = sortType.name)
                }
            }
        }
        RecordBody(
            trueRecords = state.trueRecords,
            onEvent = viewModel::onEvent
        )
    }
}

