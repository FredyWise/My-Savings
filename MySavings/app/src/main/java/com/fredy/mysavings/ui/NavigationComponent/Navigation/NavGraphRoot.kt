package com.fredy.mysavings.ui.NavigationComponent.Navigation

import BulkAddScreen
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
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
import com.fredy.mysavings.Util.Log
import com.fredy.mysavings.ViewModels.AccountViewModel
import com.fredy.mysavings.ViewModels.AuthViewModel
import com.fredy.mysavings.ViewModels.BookViewModel
import com.fredy.mysavings.ViewModels.CurrencyViewModel
import com.fredy.mysavings.ViewModels.Event.AccountEvent
import com.fredy.mysavings.ViewModels.Event.AuthEvent
import com.fredy.mysavings.ViewModels.Event.RecordsEvent
import com.fredy.mysavings.ViewModels.InputOutputViewModel
import com.fredy.mysavings.ViewModels.RecordViewModel
import com.fredy.mysavings.ViewModels.SearchViewModel
import com.fredy.mysavings.ViewModels.SettingViewModel
import com.fredy.mysavings.ui.NavigationComponent.MainScreen
import com.fredy.mysavings.ui.Screens.AddSingle.AddScreen
import com.fredy.mysavings.ui.Screens.Other.CurrencyScreen
import com.fredy.mysavings.ui.Screens.Other.ExportScreen
import com.fredy.mysavings.ui.Screens.Other.PreferencesScreen
import com.fredy.mysavings.ui.Screens.Other.ProfileScreen
import com.fredy.mysavings.ui.Search.SearchScreen
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun NavGraphRoot(
    navController: NavHostController,
    startDestination: String,
    settingViewModel: SettingViewModel,
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
            settingViewModel,
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
                val accountViewModel = entry.sharedViewModel<AccountViewModel>(navController)
                val bookViewModel = entry.sharedViewModel<BookViewModel>(navController)
                val currencyViewModel = entry.sharedViewModel<CurrencyViewModel>(navController)
                val state by authViewModel.state.collectAsStateWithLifecycle()
                val context = LocalContext.current
                MainScreen(
                    rootNavController = navController,
                    recordViewModel = recordViewModel,
                    accountViewModel = accountViewModel,
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
            ) {
                Log.d("NavGraphRoot: BulkAdd")
                BulkAddScreen(
                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    ),
                    navigateUp = { navController.navigateUp() },
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
            ) {
                Log.d("NavGraphRoot: Add")
                AddScreen(
                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    ),
                    navigateUp = { navController.navigateUp() },
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
                val state by settingViewModel.state.collectAsStateWithLifecycle()
                PreferencesScreen(
                    title = NavigationRoute.Preferences.title,
                    rootNavController = navController,
                    state = state,
                    onEvent = settingViewModel::onEvent
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
                val accountViewModel = entry.sharedViewModel<AccountViewModel>(navController)
                val currencyViewModel = entry.sharedViewModel<CurrencyViewModel>(navController)
                val state by currencyViewModel.state.collectAsStateWithLifecycle()
                CurrencyScreen(
                    title = NavigationRoute.Currency.title,
                    rootNavController = navController,
                    state = state,
                    onEvent = currencyViewModel::onEvent,
                    updateMainScreen = {
                        accountViewModel.onEvent(AccountEvent.UpdateAccount)
                        recordViewModel.onEvent(RecordsEvent.UpdateRecord)
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
            ) {entry ->
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
