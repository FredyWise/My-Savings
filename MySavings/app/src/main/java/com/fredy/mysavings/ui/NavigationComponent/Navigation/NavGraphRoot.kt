package com.fredy.mysavings.ui.NavigationComponent.Navigation

import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.fredy.mysavings.Util.TAG
import com.fredy.mysavings.ViewModels.AuthViewModel
import com.fredy.mysavings.ViewModels.Event.AuthEvent
import com.fredy.mysavings.ViewModels.RecordViewModel
import com.fredy.mysavings.ViewModels.SearchViewModel
import com.fredy.mysavings.ui.NavigationComponent.MainScreen
import com.fredy.mysavings.ui.Screens.AddBulk.BulkAddScreen
import com.fredy.mysavings.ui.Screens.AddSingle.AddScreen
import com.fredy.mysavings.ui.Screens.Other.BackupScreen
import com.fredy.mysavings.ui.Screens.Other.ExportScreen
import com.fredy.mysavings.ui.Screens.Other.PreferencesScreen
import com.fredy.mysavings.ui.Screens.Other.ProfileScreen
import com.fredy.mysavings.ui.Search.SearchScreen

@Composable
fun NavGraphRoot(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    NavHost(
        modifier = Modifier.background(
            MaterialTheme.colorScheme.background
        ),
        navController = navController,
        startDestination = Graph.FirstNav,
        route = Graph.Root,
        enterTransition = {
            fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300))
        },
    ) {
        composable(
            route = Graph.FirstNav,
        ) {
//            Box(modifier = Modifier.fillMaxSize())
            Log.d(TAG, "NavGraphRoot: ")
            val state by authViewModel.state.collectAsStateWithLifecycle()
            val startDestination = if (state.signedInUser != null) Graph.MainNav else Graph.Auth
            navController.navigateSingleTopTo(
                startDestination
            )
        }
        authenticationNavGraph(
            authViewModel,
            rootNavController = navController
        )
        navigation(
            route = Graph.MainNav,
            startDestination = Graph.HomeNav,
        ) {
            composable(
                route = Graph.HomeNav,
            ) {
                val state by authViewModel.state.collectAsStateWithLifecycle()

                state.signedInUser?.let {
                    MainScreen(
                        rootNavController = navController,
                        signOut = {
                            authViewModel.onEvent(
                                AuthEvent.SignOut
                            )
                        },
                        currentUser = it
                    )
                } ?: run {
                    Box(modifier = Modifier.fillMaxSize()) {}
                }
            }
            composable(
                route = NavigationRoute.BulkAdd.route,
            ) {
                Log.d(
                    TAG,
                    "NavGraphRoot: BulkAdd",
                )
                BulkAddScreen(
                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    ),
                    navigateUp = { navController.navigateUp() },
                )
            }
            composable(
                route = "${NavigationRoute.Add.route}/{id}",
                arguments = listOf(navArgument("id") {
                    type = NavType.StringType
                    defaultValue = "-1"
                })
            ) {
                Log.d(TAG, "NavGraphRoot: Add")
                val id = it.arguments?.getString("id") ?: "-1"
                AddScreen(
                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    ),
                    id = id,
                    navigateUp = { navController.navigateUp() },
                )
            }
            composable(
                route = NavigationRoute.Preferences.route
            ) {
                PreferencesScreen(
                    title = NavigationRoute.Preferences.title,
                    rootNavController = navController
                )
            }
            composable(
                route = NavigationRoute.Export.route
            ) {
                ExportScreen(
                    title = NavigationRoute.Export.title,
                    rootNavController = navController
                )
            }
            composable(
                route = NavigationRoute.Restore.route
            ) {
                BackupScreen(
                    title = NavigationRoute.Restore.title,
                    rootNavController = navController
                )
            }
            composable(
                route = NavigationRoute.Profile.route,
            ) {
                ProfileScreen(
                    title = NavigationRoute.Profile.title,
                    rootNavController = navController
                )
            }
            composable(
                route = NavigationRoute.Search.route,
            ) {
                val viewModel: SearchViewModel = hiltViewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()
                val recordViewModel: RecordViewModel = hiltViewModel()
                val recordState by recordViewModel.state.collectAsStateWithLifecycle()

                SearchScreen(
                    title = NavigationRoute.Search.title,
                    rootNavController = navController,
                    state = state,
                    onSearch = viewModel::onSearch,
                    recordState = recordState,
                    onEvent = recordViewModel::onEvent
                )
            }
        }

    }
}

fun NavHostController.navigateSingleTopTo(route: String) = this.navigate(
    route
) {
    popUpTo(
        this@navigateSingleTopTo.graph.startDestinationId
    ) {
        saveState = true
        inclusive = true
    }
    launchSingleTop = true
    restoreState = true

}

