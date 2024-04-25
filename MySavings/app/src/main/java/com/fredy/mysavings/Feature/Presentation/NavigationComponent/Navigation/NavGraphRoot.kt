package com.fredy.mysavings.Feature.Presentation.NavigationComponent.Navigation

import BulkAddScreen
import android.widget.Toast
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.fredy.mysavings.Feature.Presentation.NavigationComponent.MainScreen
import com.fredy.mysavings.Feature.Presentation.Screens.AddRecord.AddSingle.AddScreen
import com.fredy.mysavings.Feature.Presentation.Screens.Authentication.ProfileScreen
import com.fredy.mysavings.Feature.Presentation.Screens.Currency.CurrencyScreen
import com.fredy.mysavings.Feature.Presentation.Screens.IO.ExportScreen
import com.fredy.mysavings.Feature.Presentation.Screens.Preference.PreferencesScreen
import com.fredy.mysavings.Feature.Presentation.ViewModels.AddRecordViewModel.AddSingleRecordViewModel
import com.fredy.mysavings.Feature.Presentation.ViewModels.AuthViewModel.AuthEvent
import com.fredy.mysavings.Feature.Presentation.ViewModels.AuthViewModel.AuthViewModel
import com.fredy.mysavings.Feature.Presentation.ViewModels.BookViewModel.BookViewModel
import com.fredy.mysavings.Feature.Presentation.ViewModels.CategoryViewModel.CategoryViewModel
import com.fredy.mysavings.Feature.Presentation.ViewModels.CurrencyViewModel.CurrencyViewModel
import com.fredy.mysavings.Feature.Presentation.ViewModels.IOViewModel.InputOutputViewModel
import com.fredy.mysavings.Feature.Presentation.ViewModels.PreferencesViewModel.PreferencesViewModel
import com.fredy.mysavings.Feature.Presentation.ViewModels.RecordViewModel.RecordEvent
import com.fredy.mysavings.Feature.Presentation.ViewModels.RecordViewModel.RecordViewModel
import com.fredy.mysavings.Feature.Presentation.ViewModels.SearchViewModel.SearchViewModel
import com.fredy.mysavings.Feature.Presentation.ViewModels.WalletViewModel.WalletEvent
import com.fredy.mysavings.Feature.Presentation.ViewModels.WalletViewModel.WalletViewModel
import com.fredy.mysavings.Util.Log
import com.fredy.mysavings.ui.Search.SearchScreen
import kotlinx.coroutines.ExperimentalCoroutinesApi

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
            preferencesViewModel,
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
                val walletViewModel = entry.sharedViewModel<WalletViewModel>(navController)
                val categoryViewModel = entry.sharedViewModel<CategoryViewModel>(navController)
                val bookViewModel = entry.sharedViewModel<BookViewModel>(navController)
                val currencyViewModel = entry.sharedViewModel<CurrencyViewModel>(navController)
                val state by authViewModel.state.collectAsStateWithLifecycle()
                val context = LocalContext.current
                MainScreen(
                    rootNavController = navController,
                    recordViewModel = recordViewModel,
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
                route = NavigationRoute.BulkAdd.route,
                enterTransition = {
                    fadeIn()
                },
                exitTransition = {
                    fadeOut()
                },
            ) { entry ->
                val walletViewModel: WalletViewModel = hiltViewModel()
                val categoryViewModel: CategoryViewModel = hiltViewModel()
                val viewModel: AddSingleRecordViewModel = hiltViewModel()
                Log.d("NavGraphRoot: BulkAdd")
                BulkAddScreen(
                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    ),
                    navigateUp = { navController.navigateUp() },
                    viewModel = viewModel,
                    walletViewModel = walletViewModel,
                    categoryViewModel = categoryViewModel
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
                val categoryViewModel: CategoryViewModel = hiltViewModel()
                val viewModel: AddSingleRecordViewModel = hiltViewModel()
                Log.d("NavGraphRoot: Add")
                AddScreen(
                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    ),
                    navigateUp = { navController.navigateUp() },
                    viewModel = viewModel,
                    walletViewModel = walletViewModel,
                    categoryViewModel = categoryViewModel
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
                val recordState by recordViewModel.state.collectAsStateWithLifecycle()
                val bookViewModel = entry.sharedViewModel<BookViewModel>(navController)
                val bookState by bookViewModel.state.collectAsStateWithLifecycle()

                SearchScreen(
                    title = NavigationRoute.Search.title,
                    rootNavController = navController,
                    state = state,
                    onSearch = viewModel::onSearch,
                    recordState = recordState,
                    onEvent = recordViewModel::onEvent,
                    bookState = bookState,
                    bookEvent = bookViewModel::onEvent
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
