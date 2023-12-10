package com.fredy.mysavings.ui.Screens.Analysis


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fredy.mysavings.Util.ResourceState
import com.fredy.mysavings.Util.formatRangeOfDate
import com.fredy.mysavings.ViewModel.AnalysisState
import com.fredy.mysavings.ViewModels.Event.AnalysisEvent
import com.fredy.mysavings.ui.NavigationComponent.AdditionalAppBar
import com.fredy.mysavings.ui.NavigationComponent.Navigation.AnalysisNavGraph
import com.fredy.mysavings.ui.NavigationComponent.Navigation.NavigationRoute
import com.fredy.mysavings.ui.NavigationComponent.Navigation.analysisScreens
import com.fredy.mysavings.ui.NavigationComponent.Navigation.navigateSingleTopTo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    modifier: Modifier = Modifier,
    rootNavController: NavHostController,
    state: AnalysisState,
    resource: ResourceState,
    onEvent: (AnalysisEvent) -> Unit,
) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentScreen = analysisScreens.find { it.route == currentDestination?.route } ?: NavigationRoute.Records
    var expanded by remember {
        mutableStateOf(
            false
        )
    }
    val icon = if (expanded) Icons.Filled.KeyboardArrowUp
    else Icons.Filled.KeyboardArrowDown

    AdditionalAppBar(
        modifier = modifier,
        selectedDate = state.selectedDate,
        onDateChange = {
            onEvent(
                AnalysisEvent.ChangeDate(it)
            )
        },
        selectedDateFormat = formatRangeOfDate(
            state.selectedDate, state.filterType
        ),
        isChoosingFilter = state.isChoosingFilter,
        selectedFilter = state.filterType.name,
        onDismissFilterDialog = {
            onEvent(AnalysisEvent.HideFilterDialog)
        },
        onSelectFilter = {
            onEvent(
                AnalysisEvent.FilterRecord(it)
            )
        },
        totalExpense = state.totalExpense,
        totalIncome = state.totalIncome,
        totalBalance = state.totalAll,
        leadingIcon = {
            Icon(
                modifier = Modifier
                    .clip(
                        MaterialTheme.shapes.extraLarge
                    )
                    .clickable {

                    }
                    .padding(4.dp),
                imageVector = Icons.Outlined.Info,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSurface,
            )
        },
        trailingIcon = {
            Icon(
                modifier = Modifier
                    .clip(
                        MaterialTheme.shapes.extraLarge
                    )
                    .clickable {
                        onEvent(AnalysisEvent.ShowFilterDialog)
                    }
                    .padding(4.dp),
                imageVector = Icons.Default.FilterList,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSurface,
            )
        },
        onPrevious = { onEvent(AnalysisEvent.ShowPreviousList) },
        onNext = { onEvent(AnalysisEvent.ShowNextList) },
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ExposedDropdownMenuBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 50.dp,
                        vertical = 8.dp
                    ),
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
            ) {
                TextField(
                    value = currentScreen.title,
                    enabled = false,
                    singleLine = true,
                    onValueChange = {
                        expanded = true
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .clip(
                            MaterialTheme.shapes.medium
                        ),
                    colors = TextFieldDefaults.colors(
                        disabledIndicatorColor = Color.Unspecified,
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface
                    ),
                    trailingIcon = {
                        Icon(
                            icon,
                            "contentDescription",
                        )
                    },
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    analysisScreens.forEach { screen ->
                        DropdownMenuItem(
                            onClick = {
                                expanded = false
                                navController.navigateSingleTopTo(
                                    screen.route
                                )
                            },
                            text = {
                                Text(
                                    text = screen.title,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            },
                        )
                    }
                }
            }
            AnalysisNavGraph(
                rootNavController = rootNavController,
                navController = navController,
                state = state,
                resource = resource,
                onEvent = onEvent,
            )
        }
    }
}

