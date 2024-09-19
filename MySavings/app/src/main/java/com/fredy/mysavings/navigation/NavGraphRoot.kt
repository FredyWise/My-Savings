package com.fredy.mysavings.navigation

import android.widget.Toast
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.fredy.addrecord.ui.AddBottomSheet
import com.fredy.addrecord.ui.bullkAdd.BulkAddScreen
import com.fredy.addrecord.ui.bullkAdd.RecordAddDialog
import com.fredy.addrecord.ui.singleAdd.AddScreen
import com.fredy.addrecord.viewModel.AddRecordEvent
import com.fredy.addrecord.viewModel.BulkAddRecordEvent
import com.fredy.addrecord.viewModel.bulkAdd.AddBulkRecordViewModel
import com.fredy.addrecord.viewModel.singleAdd.AddSingleRecordViewModel
import com.fredy.analysis.viewModel.AnalysisViewModel
import com.fredy.auth.authenticationNavGraph
import com.fredy.auth.ui.ProfileScreen
import com.fredy.auth.viewModel.AuthEvent
import com.fredy.auth.viewModel.AuthViewModel
import com.fredy.book.ui.BookAddDialog
import com.fredy.book.ui.RecordDialog
import com.fredy.book.viewModel.BookEvent
import com.fredy.book.viewModel.BookViewModel
import com.fredy.book.viewModel.RecordEvent
import com.fredy.book.viewModel.RecordViewModel
import com.fredy.category.ui.CategoryAddDialog
import com.fredy.category.viewModel.CategoryEvent
import com.fredy.category.viewModel.CategoryViewModel
import com.fredy.currency.ui.CurrencyScreen
import com.fredy.currency.viewModel.CurrencyViewModel
import com.fredy.domain.enums.RecordType
import com.fredy.domain.enumsChecker.isTransfer
import com.fredy.io.ui.ExportScreen
import com.fredy.io.viewModel.InputOutputViewModel
import com.fredy.mysavings.screen.MainScreen
import com.fredy.preferences.ui.PreferencesScreen
import com.fredy.preferences.viewModel.PreferencesViewModel
import com.fredy.search.ui.SearchScreen
import com.fredy.search.viewModel.SearchViewModel
import com.fredy.wallet.ui.WalletAddDialog
import com.fredy.wallet.viewModel.WalletEvent
import com.fredy.wallet.viewModel.WalletViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun NavGraphRoot(
    navController: NavHostController,
    startDestination: String,
    preferencesViewModel: PreferencesViewModel,
    authViewModel: AuthViewModel,
) {
    NavHost(
        modifier = Modifier.background(
            MaterialTheme.colorScheme.background
        ),
        navController = navController,
        route = Graph.RootNav,
        startDestination = startDestination,
    ) {
        authenticationNavGraph(
            preferencesViewModel.state.value.bioAuth,
            authViewModel,
            rootNavController = navController
        )
        navigation(
            route = Graph.MainNav,
            startDestination = Graph.HomeNav,
        ) {
            composable(
                route = Graph.HomeNav,
                enterTransition = {
                    fadeIn()
                },
                exitTransition = {
                    fadeOut()
                },
            ) { entry ->
                val recordViewModel = entry.sharedViewModel<RecordViewModel>(navController)
                val analysisViewModel = entry.sharedViewModel<AnalysisViewModel>(navController)
                val walletViewModel = entry.sharedViewModel<WalletViewModel>(navController)
                val categoryViewModel = entry.sharedViewModel<CategoryViewModel>(navController)
                val bookViewModel = entry.sharedViewModel<BookViewModel>(navController)
                val currencyViewModel = entry.sharedViewModel<CurrencyViewModel>(navController)
                val state by authViewModel.state.collectAsStateWithLifecycle()
                val context = LocalContext.current
                MainScreen(
                    rootNavController = navController,
                    recordViewModel = recordViewModel,
                    analysisViewModel = analysisViewModel,
                    walletViewModel = walletViewModel,
                    categoryViewModel = categoryViewModel,
                    bookViewModel = bookViewModel,
                    signOut = {
                        authViewModel.onEvent(
                            AuthEvent.SignOut
                        )
                        Toast.makeText(
                            context,
                            "Signed out",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.navigate(
                            Graph.AuthNav
                        )
                    },
                    currentUser = state.signedInUser
                )
            }
            composable(
                route = "${NavigationRoute.BulkAdd.route}?bookId={bookId}",
                enterTransition = {
                    fadeIn()
                },
                exitTransition = {
                    fadeOut()
                },
                arguments = listOf(
                    navArgument(
                        name = "bookId"
                    ) {
                        type = NavType.StringType
                        defaultValue = null
                        nullable = true
                    },
                )
            ) { entry ->
                val walletViewModel: WalletViewModel = hiltViewModel()
                val walletState by walletViewModel.state.collectAsStateWithLifecycle()
                val walletEvent = walletViewModel::onEvent
                val walletResource = walletState.walletResource
                val categoryViewModel: CategoryViewModel = hiltViewModel()
                val categoryState by categoryViewModel.state.collectAsStateWithLifecycle()
                val categoryEvent = categoryViewModel::onEvent
                val categoryResource = categoryState.categoryResource
                val viewModel: AddBulkRecordViewModel = hiltViewModel()
                val state = viewModel.state
                val resource by viewModel.resource.collectAsStateWithLifecycle()
                val onEvent = viewModel::onEvent
                RecordAddDialog(
                    record = state.record,
                    isAdding = state.isAdding,
                    isShowDialog = state.isShowWarning,
                    onDismissRequest = { onEvent(BulkAddRecordEvent.CloseAddRecordItemDialog) },
                    onSave = {
                        onEvent(BulkAddRecordEvent.UpdateRecord(it))
                    },
                    onDelete = {
                        onEvent(BulkAddRecordEvent.DeleteRecord(it))
                    }
                )
                val scope = rememberCoroutineScope()
                val sheetState = rememberModalBottomSheetState()
                var recordType by remember {
                    mutableStateOf(RecordType.Expense)
                }
                var isSheetOpen by rememberSaveable {
                    mutableStateOf(false)
                }
                var isLeading by remember {
                    mutableStateOf(
                        true
                    )
                }
                if (isSheetOpen) {
                    AddBottomSheet(
                        sheetState = sheetState,
                        onDismissModal = {
                            scope.launch {
                                isSheetOpen = it
                            }
                        },
                        isLeading = isLeading,
                        recordType = recordType,

                        walletResource = walletResource,
                        categoryResource = categoryResource,
                        showWalletDialog = {
                            walletEvent(
                                WalletEvent.ShowDialog(
                                    it
                                )
                            )
                        },
                        showCategoryDialog = {
                            categoryEvent(
                                CategoryEvent.ShowDialog(
                                    it
                                )
                            )
                        },
                        onSelectFromAccount = {
                            onEvent(
                                AddRecordEvent.AccountIdFromFk(
                                    it
                                )
                            )
                        },
                        onSelectToAccount = {
                            onEvent(
                                AddRecordEvent.AccountIdToFk(
                                    it
                                )
                            )
                        },
                        onSelectCategory = {
                            onEvent(
                                AddRecordEvent.CategoryIdFk(
                                    it
                                )
                            )
                        }
                    )
                }
                WalletAddDialog(
                    state = walletState,
                    onEvent = walletEvent
                )
                CategoryAddDialog(
                    state = categoryState,
                    onEvent = categoryEvent
                )
                BulkAddScreen(
                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    ),
                    state = state,
                    onEvent = onEvent,
                    resource = resource,
                    navigateUp = { navController.navigateUp() },
                    onConvertImageToRecord = {
                        onEvent(BulkAddRecordEvent.ImageToRecords(it))
                    },
                    onRecordNotesChange = {
                        onEvent(AddRecordEvent.RecordNotes(it))
                    },
                    onShowAddRecordItemDialog = {
                        onEvent(BulkAddRecordEvent.ShowAddRecordItemDialog(it))
                    },
                    onSaveRecordClick = {
                        onEvent(
                            AddRecordEvent.SaveRecord {
                                walletViewModel.onEvent(
                                    WalletEvent.UpdateWalletBalance(
                                        state.fromWallet
                                    )
                                )
                                navController.navigateUp()
                            },
                        )
                    },
                    onTopButtonClick = {
                        isLeading = true
                        scope.launch {
                            isSheetOpen = true
                        }
                    },
                    onLeftButtonClick = {
                        isLeading = true
                        recordType = RecordType.Expense
                        scope.launch {
                            isSheetOpen = true
                        }
                    },
                    onRightButtonClick = {
                        isLeading = false
                        recordType = RecordType.Income
                        scope.launch {
                            isSheetOpen = true
                        }
                    }

                )
            }
            composable(
                route = "${NavigationRoute.Add.route}?recordId={recordId}&bookId={bookId}",
                enterTransition = {
                    fadeIn()
                },
                exitTransition = {
                    fadeOut()
                },
                arguments = listOf(
                    navArgument(
                        name = "recordId"
                    ) {
                        type = NavType.StringType
                        defaultValue = "-1"
                    },
                    navArgument(
                        name = "bookId"
                    ) {
                        type = NavType.StringType
                        defaultValue = null
                        nullable = true
                    },
                )
            ) { entry ->
                val walletViewModel: WalletViewModel = hiltViewModel()
                val walletState by walletViewModel.state.collectAsStateWithLifecycle()
                val walletEvent = walletViewModel::onEvent
                val walletResource = walletState.walletResource
                val categoryViewModel: CategoryViewModel = hiltViewModel()
                val categoryState by categoryViewModel.state.collectAsStateWithLifecycle()
                val categoryEvent = categoryViewModel::onEvent
                val categoryResource = categoryState.categoryResource
                val viewModel: AddSingleRecordViewModel = hiltViewModel()
                val state = viewModel.state
                val resource by viewModel.resource.collectAsStateWithLifecycle()
                val onEvent = viewModel::onEvent
                val onAction = viewModel::onAction
                val calculatorState = viewModel.calcState
                Timber.d("NavGraphRoot: Add")

                val scope = rememberCoroutineScope()
                var isLeading by remember {
                    mutableStateOf(
                        true
                    )
                }
                val sheetState = rememberModalBottomSheetState()
                var isSheetOpen by rememberSaveable {
                    mutableStateOf(false)
                }
                if (isSheetOpen) {
                    AddBottomSheet(
                        sheetState = sheetState,
                        onDismissModal = {
                            scope.launch {
                                isSheetOpen = it
                            }
                        },
                        isLeading = isLeading,
                        recordType = state.recordType,
                        walletResource = walletResource,
                        categoryResource = categoryResource,
                        showWalletDialog = {
                            walletEvent(
                                WalletEvent.ShowDialog(
                                    it
                                )
                            )
                        },
                        showCategoryDialog = {
                            categoryEvent(
                                CategoryEvent.ShowDialog(
                                    it
                                )
                            )
                        },
                        onSelectFromAccount = {
                            onEvent(
                                AddRecordEvent.AccountIdFromFk(
                                    it
                                )
                            )
                        },
                        onSelectToAccount = {
                            onEvent(
                                AddRecordEvent.AccountIdToFk(
                                    it
                                )
                            )
                        },
                        onSelectCategory = {
                            onEvent(
                                AddRecordEvent.CategoryIdFk(
                                    it
                                )
                            )
                        }
                    )
                }
                WalletAddDialog(
                    state = walletState,
                    onEvent = walletEvent
                )
                if (categoryState.isAddingCategory) {
                    if (state.recordType != categoryState.categoryType) {
                        categoryViewModel.onEvent(
                            CategoryEvent.CategoryTypes(
                                state.recordType
                            )
                        )
                    }
                    CategoryAddDialog(
                        state = categoryState,
                        onEvent = categoryEvent
                    )
                }
                AddScreen(
                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    ),
                    state = state,
                    onEvent = onEvent,
                    calculatorState = calculatorState,
                    onAction = onAction,
                    resource = resource,
                    navigateUp = { navController.navigateUp() },
                    onSaveClick = {
                        onEvent(
                            AddRecordEvent.SaveRecord {
                                walletViewModel.onEvent(
                                    WalletEvent.UpdateWalletBalance(
                                        state.fromWallet
                                    )
                                )
                                if (state.recordType.isTransfer()) {
                                    walletViewModel.onEvent(
                                        WalletEvent.UpdateWalletBalance(
                                            state.toWallet
                                        )
                                    )
                                }
                                navController.navigateUp()
                            },
                        )
                    },

                    onLeftButtonClick = {
                        isLeading = true
                        scope.launch {
                            isSheetOpen = true
                        }
                    },
                    onRightButtonClick = {
                        isLeading = false
                        scope.launch {
                            isSheetOpen = true
                        }
                    },
                )
            }
            composable(
                route = NavigationRoute.Preferences.route,
                enterTransition = {
                    fadeIn()
                },
                exitTransition = {
                    fadeOut()
                },
            ) {
                val state by preferencesViewModel.state.collectAsStateWithLifecycle()
                PreferencesScreen(
                    title = NavigationRoute.Preferences.title,
                    rootNavController = navController,
                    state = state,
                    onEvent = preferencesViewModel::onEvent
                )
            }
            composable(
                route = NavigationRoute.Export.route,
                enterTransition = {
                    fadeIn()
                },
                exitTransition = {
                    fadeOut()
                },
            ) {
                val viewModel: InputOutputViewModel = hiltViewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()
                ExportScreen(
                    title = NavigationRoute.Export.title,
                    rootNavController = navController,
                    state = state,
                    onEvent = viewModel::onEvent
                )
            }
            composable(
                route = NavigationRoute.Currency.route,
                enterTransition = {
                    fadeIn()
                },
                exitTransition = {
                    fadeOut()
                },
            ) { entry ->
                val recordViewModel = entry.sharedViewModel<RecordViewModel>(navController)
                val walletViewModel = entry.sharedViewModel<WalletViewModel>(navController)
                val currencyViewModel = entry.sharedViewModel<CurrencyViewModel>(navController)
                val state by currencyViewModel.state.collectAsStateWithLifecycle()
                CurrencyScreen(
                    title = NavigationRoute.Currency.title,
                    rootNavController = navController,
                    state = state,
                    onEvent = currencyViewModel::onEvent,
                    updateMainScreen = {
                        walletViewModel.onEvent(WalletEvent.UpdateWallet)
                        recordViewModel.onEvent(RecordEvent.UpdateRecord)
                    }
                )
            }
            composable(
                route = NavigationRoute.Profile.route,
                enterTransition = {
                    fadeIn()
                },
                exitTransition = {
                    fadeOut()
                },
            ) {
                val state by authViewModel.state.collectAsStateWithLifecycle()

                state.signedInUser?.let {
                    ProfileScreen(
                        rootNavController = navController,
                        title = NavigationRoute.Profile.title,
                        currentUserData = it,
                        state = state,
                        onEvent = authViewModel::onEvent
                    )
                } ?: run {
                    Box(modifier = Modifier.fillMaxSize())
                }
            }
            composable(
                route = NavigationRoute.Search.route,
                enterTransition = {
                    fadeIn()
                },
                exitTransition = {
                    fadeOut()
                },
            ) { entry ->
                val viewModel: SearchViewModel = hiltViewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()
                val recordViewModel = entry.sharedViewModel<RecordViewModel>(navController)
                val recordEvent = recordViewModel::onEvent
                val recordState by recordViewModel.state.collectAsStateWithLifecycle()
                val bookViewModel = entry.sharedViewModel<BookViewModel>(navController)
                val bookEvent = bookViewModel::onEvent
                val bookState by bookViewModel.state.collectAsStateWithLifecycle()
                BookAddDialog(state = bookState, onEvent = bookEvent)
                recordState.trueRecord?.let {
                    RecordDialog(
                        trueRecord = it,
                        onSaveClicked = { record ->
                            recordEvent(
                                RecordEvent.DeleteRecord(
                                    record
                                )
                            )
                        },
                        onDismissDialog = {
                            recordEvent(
                                RecordEvent.HideDialog
                            )
                        },
                        onEdit = {
                            navController.navigate(
                                "${NavigationRoute.Add.route}?recordId=${it.record.recordId}&bookId=${it.record.bookIdFk}"
                            )
                        },
                    )

                }
                SearchScreen(
                    title = NavigationRoute.Search.title,
                    rootNavController = navController,
                    state = state,
                    onSearch = viewModel::onSearch,
                    onShowRecordDialog = {
                        recordEvent(RecordEvent.ShowDialog(it))
                    },
                    onShowBookDialog = {
                        bookEvent(BookEvent.ShowDialog(it))
                    },
                    onAddBook = {
                        navController.navigate(
                            "${NavigationRoute.Add.route}?bookId=${recordState.filterState.currentBook?.bookId}"
                        )
                    }
                )
            }
        }

    }
}

fun NavHostController.navigateSingleTopTo(route: String) = this.navigate(
    route
) {
    popUpTo(
        this@navigateSingleTopTo.graph.findStartDestination().id
    ) {
        saveState = true
    }
    launchSingleTop = true
    restoreState = true

}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavHostController,
): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}
