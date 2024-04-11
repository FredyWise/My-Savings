package com.fredy.mysavings.ui.NavigationComponent

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fredy.mysavings.Feature.Data.Database.Model.UserData
import com.fredy.mysavings.Util.formatRangeOfDate
import com.fredy.mysavings.ViewModels.AccountViewModel
import com.fredy.mysavings.ViewModels.BookViewModel
import com.fredy.mysavings.ViewModels.CategoryViewModel
import com.fredy.mysavings.ViewModels.Event.RecordsEvent
import com.fredy.mysavings.ViewModels.RecordViewModel
import com.fredy.mysavings.ui.NavigationComponent.Navigation.HomeNavGraph
import com.fredy.mysavings.ui.NavigationComponent.Navigation.NavigationRoute
import com.fredy.mysavings.ui.NavigationComponent.Navigation.bottomBarScreens
import com.fredy.mysavings.ui.NavigationComponent.Navigation.drawerScreens
import com.fredy.mysavings.ui.NavigationComponent.Navigation.navigateSingleTopTo
import com.fredy.mysavings.ui.Screens.Record.RecordDialog
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SimpleWarningDialog
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = MaterialTheme.colorScheme.surface,
    onContentColor: Color = MaterialTheme.colorScheme.onSurface,
    rootNavController: NavHostController,
    recordViewModel: RecordViewModel,
    accountViewModel: AccountViewModel,
    categoryViewModel: CategoryViewModel = hiltViewModel(),
    bookViewModel: BookViewModel,
    currentUser: UserData?,
    signOut: () -> Unit,
) {

    val state by recordViewModel.state.collectAsStateWithLifecycle()
    val onEvent = recordViewModel::onEvent
    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
//    var offsetX by remember { mutableStateOf(0f) }
//    var offsetY by remember { mutableStateOf(0f) }
//    var isFabVisible by remember {
//        mutableStateOf(
//            true
//        )
//    }

//    var isShowingAdd by remember {
//        mutableStateOf(
//            false
//        )
//    }
    val scope = rememberCoroutineScope()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentScreen =
        bottomBarScreens.find { it.route == currentDestination?.route } ?: NavigationRoute.Records

    var isShowWarning by remember { mutableStateOf(false) }
    SimpleWarningDialog(
        isShowWarning = isShowWarning,
        onDismissRequest = { isShowWarning = false },
        onSaveClicked = {
            signOut()
        },
        warningText = "Are You Sure Want to Log Out?"
    )
    state.trueRecord?.let {
        RecordDialog(
            trueRecord = it,
            onSaveClicked = { record ->
                onEvent(
                    RecordsEvent.DeleteRecord(
                        record
                    )
                )
            },
            onDismissDialog = {
                onEvent(
                    RecordsEvent.HideDialog
                )
            },
            onEdit = {
                rootNavController.navigate(
                    "${NavigationRoute.Add.route}?recordId=${it.record.recordId}&bookId=${it.record.bookIdFk}"
                )
            },
        )
    }
    Scaffold(
        modifier = modifier,
        backgroundColor = backgroundColor,
        scaffoldState = scaffoldState,
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
        drawerBackgroundColor = contentColor,
        drawerContentColor = onContentColor,
        drawerContent = {
            DrawerHeader()
            DrawerBody(
                items = drawerScreens,
                onItemClick = { newScreen ->
                    rootNavController.navigate(
                        newScreen.route
                    )
                },
                additionalItem = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                isShowWarning = true
                            }
                            .padding(16.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(
                            modifier = Modifier.width(
                                16.dp
                            )
                        )
                        Text(
                            text = "Sign Out",
                            style = TextStyle(
                                fontSize = 18.sp
                            ),
                            modifier = Modifier.weight(
                                1f
                            )
                        )
                    }
                },
            )
        },
        bottomBar = {
            BottomBar(
                modifier = Modifier.height(
                    65.dp
                ),
                backgroundColor = contentColor,
                contentColor = onContentColor,
                allScreens = bottomBarScreens,
                onTabSelected = { newScreen ->
                    navController.navigateSingleTopTo(
                        newScreen.route
                    )
//                    isShowingAdd = false
                },
                currentScreen = currentScreen
            )
        },
        floatingActionButton = {
//            AnimatedVisibility(visible = isFabVisible) {
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
//            ) {
//                AnimatedVisibility(
//                    visible = isShowingAdd,
//                    enter = fadeIn(animationSpec = tween(300)),
//                    exit = fadeOut(animationSpec = tween(300)),
//                ) {
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                    ) {
            FloatingActionButton(
                onClick = {
                    rootNavController.navigate(
                        "${NavigationRoute.Add.route}?bookId=${state.filterState.currentBook?.bookId}"
                    )
                },
                backgroundColor = contentColor,
                modifier = Modifier.border(
                    1.dp,
                    MaterialTheme.colorScheme.secondary.copy(
                        0.3f
                    ),
                    CircleShape
                ),
            ) {
                Icon(
//                                NavigationRoute.Add.icon,
                    Icons.Default.Add,
                    modifier = Modifier.size(
                        30.dp
                    ),
                    tint = onContentColor,
                    contentDescription = ""
                )
            }
//                        Spacer(
//                            modifier = Modifier.height(
//                                8.dp
//                            )
//                        )
//                        FloatingActionButton(
//                            onClick = {
//                                rootNavController.navigate(
//                                    NavigationRoute.BulkAdd.route
//                                )
//                            },
//                            backgroundColor = contentColor,
//                            modifier = Modifier.border(
//                                1.dp,
//                                MaterialTheme.colorScheme.secondary.copy(
//                                    0.3f
//                                ),
//                                CircleShape
//                            ),
//                        ) {
//                            Icon(
//                                NavigationRoute.BulkAdd.icon,
//                                modifier = Modifier.size(
//                                    30.dp
//                                ),
//                                tint = onContentColor,
//                                contentDescription = ""
//                            )
//                        }
//                        Spacer(
//                            modifier = Modifier.height(
//                                8.dp
//                            )
//                        )
//                    }
//                }
//                FloatingActionButton(
//                    onClick = {
//                        isShowingAdd = !isShowingAdd
//                    },
//                    backgroundColor = contentColor,
//                    modifier = Modifier.border(
//                        1.dp,
//                        MaterialTheme.colorScheme.secondary.copy(
//                            0.3f
//                        ),
//                        CircleShape
//                    ),
//                ) {
//                    Icon(
//                        Icons.Default.Add,
//                        modifier = Modifier.size(
//                            35.dp
//                        ),
//                        tint = onContentColor,
//                        contentDescription = ""
//                    )
//                }
//            }
//            }
        },

        ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
//                .pointerInput(
//                    Unit
//                ) {
//                    detectVerticalDragGestures()
//                },
        ) {
            Column(
                modifier = Modifier
                    .shadow(elevation = 4.dp, clip = true)
            ) {
                AppBar(
                    backgroundColor = contentColor,
                    contentColor = onContentColor,
                    onNavigationIconClick = {
                        scope.launch {
                            scaffoldState.drawerState.open()
                        }
                    },
                    onProfilePictureClick = {
                        rootNavController.navigate(
                            NavigationRoute.Profile.route
                        )
                    },
                    currentUser = currentUser
                )
                MainFilterAppBar(
                    modifier = modifier,
                    onShowAppBar = currentScreen == NavigationRoute.Records || currentScreen == NavigationRoute.Analysis,
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
                    sortType = state.filterState.sortType,
                    showTotal = state.filterState.showTotal,
                    carryOn = state.filterState.carryOn,
                    useUserCurrency = state.filterState.useUserCurrency,
                    onUserCurrencyChange = { onEvent(RecordsEvent.ToggleUserCurrency) },
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
                )
            }
            HomeNavGraph(
                rootNavController = rootNavController,
                navController = navController,
                modifier = Modifier.padding(
                    innerPadding
                ),
                recordViewModel = recordViewModel,
                accountViewModel = accountViewModel,
                categoryViewModel = categoryViewModel,
                bookViewModel = bookViewModel,
            )

        }
    }
}