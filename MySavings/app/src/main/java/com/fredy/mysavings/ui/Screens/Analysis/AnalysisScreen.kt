package com.fredy.mysavings.ui.Screens.Analysis


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Search
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
import com.fredy.mysavings.Data.Enum.SortType
import com.fredy.mysavings.Util.formatRangeOfDate
import com.fredy.mysavings.ViewModels.Event.RecordsEvent
import com.fredy.mysavings.ViewModels.RecordState
import com.fredy.mysavings.ui.NavigationComponent.MainFilterAppBar
import com.fredy.mysavings.ui.NavigationComponent.Navigation.AnalysisNavGraph
import com.fredy.mysavings.ui.NavigationComponent.Navigation.NavigationRoute
import com.fredy.mysavings.ui.NavigationComponent.Navigation.analysisScreens
import com.fredy.mysavings.ui.NavigationComponent.Navigation.navigateSingleTopTo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    modifier: Modifier = Modifier,
    rootNavController: NavHostController,
    state: RecordState,
    onEvent: (RecordsEvent) -> Unit,
) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentScreen =
        analysisScreens.find { it.route == currentDestination?.route } ?: NavigationRoute.Records
    var expanded by remember {
        mutableStateOf(
            false
        )
    }
    val icon = if (expanded) Icons.Filled.KeyboardArrowUp
    else Icons.Filled.KeyboardArrowDown
    MainFilterAppBar(
        modifier = modifier,
        selectedDate = state.filterState.selectedDate,
        onDateChange = {
            onEvent(
                RecordsEvent.ChangeDate(it)
            )
        },
        selectedDateFormat = formatRangeOfDate(
            state.filterState.selectedDate,
            state.filterState.filterType
        ),
        isChoosingFilter = state.isChoosingFilter,
        selectedFilter = state.filterState.filterType.name,
        checkboxesFilter = state.availableCurrency,
        selectedCheckbox = state.selectedCheckbox,
        onDismissFilterDialog = {
            onEvent(RecordsEvent.HideFilterDialog)
        },
        onSelectFilter = {
            onEvent(
                RecordsEvent.FilterRecord(it)
            )
        },
        onSelectCheckboxFilter = {
            onEvent(
                RecordsEvent.SelectedCurrencies(it)
            )
        },
        balanceBar = state.balanceBar,
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSurface,
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSurface,
            )
        },
        sortType = when (state.filterState.sortType) {
            SortType.ASCENDING -> true
            SortType.DESCENDING -> false
        },
        showTotal = state.filterState.showTotal,
        carryOn = state.filterState.carryOn,
        onShowTotalChange = { onEvent(RecordsEvent.ToggleShowTotal) },
        onCarryOnChange = { onEvent(RecordsEvent.ToggleCarryOn) },
        onShortChange = { onEvent(RecordsEvent.ToggleSortType) },
        onPrevious = { onEvent(RecordsEvent.ShowPreviousList) },
        onNext = { onEvent(RecordsEvent.ShowNextList) },
        onLeadingIconClick = {
            rootNavController.navigate(
                NavigationRoute.Search.route
            )
        },
        onTrailingIconClick = {
            onEvent(RecordsEvent.ShowFilterDialog)
        },
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AnalysisTabRow(
                allScreens = analysisScreens,
                onTabSelected = { screen ->
                    navController.navigateSingleTopTo(
                        screen.route
                    )
                },
                currentScreen = currentScreen,
            )
//            ExposedDropdownMenuBox(
//                modifier = Modifier.padding(
//                    vertical = 8.dp
//                ),
//                expanded = expanded,
//                onExpandedChange = { expanded = !expanded },
//            ) {
//                TextField(
//                    value = currentScreen.title,
//                    enabled = false,
//                    singleLine = true,
//                    onValueChange = {
//                        expanded = true
//                    },
//                    modifier = Modifier
//                        .menuAnchor()
//                        .clip(
//                            MaterialTheme.shapes.medium
//                        ),
//                    colors = TextFieldDefaults.colors(
//                        disabledIndicatorColor = Color.Unspecified,
//                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
//                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,
//                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
//                        focusedContainerColor = MaterialTheme.colorScheme.surface,
//                        disabledContainerColor = MaterialTheme.colorScheme.surface,
//                    ),
//                    trailingIcon = {
//                        Icon(
//                            icon,
//                            "contentDescription",
//                        )
//                    },
//                )
//                ExposedDropdownMenu(
//                    expanded = expanded,
//                    onDismissRequest = { expanded = false },
//                ) {
//                    analysisScreens.forEach { screen ->
//                        DropdownMenuItem(
//                            onClick = {
//                                expanded = false
//                                navController.navigateSingleTopTo(
//                                    screen.route
//                                )
//                            },
//                            text = {
//                                Text(
//                                    text = screen.title,
//                                    color = MaterialTheme.colorScheme.onBackground
//                                )
//                            },
//                        )
//                    }
//                }
//            }
            AnalysisNavGraph(
                rootNavController = rootNavController,
                navController = navController,
                state = state,
                onEvent = onEvent,
            )
        }
    }
}

