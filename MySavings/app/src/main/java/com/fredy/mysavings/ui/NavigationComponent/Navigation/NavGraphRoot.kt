package com.fredy.mysavings.ui.NavigationComponent.Navigation

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.fredy.mysavings.Util.TAG
import com.fredy.mysavings.ViewModels.AuthViewModel
import com.fredy.mysavings.ViewModels.MainScreenViewModel
import com.fredy.mysavings.ui.NavigationComponent.MainScreen
import com.fredy.mysavings.ui.Screens.AddBulk.BulkAddScreen
import com.fredy.mysavings.ui.Screens.AddSingle.AddScreen

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
        route = Graph.Root
    ) {
        composable(
            route = Graph.FirstNav
        ) {
            Log.e(TAG, "NavGraphRoot: ")
            val state by authViewModel.state.collectAsStateWithLifecycle()
            val startDestination = if (state.signedInUser != null) Graph.HomeNav else Graph.Auth
            Log.e(TAG, "NavGraphRoot: ")
            navController.navigateSingleTopTo(
                startDestination
            )
        }
        authenticationNavGraph(
            authViewModel,
            navController = navController
        )
        navigation(
            route = Graph.MainNav,
            startDestination = Graph.HomeNav,
        ) {
            composable(
                route = Graph.HomeNav
            ) {
                val viewModel: MainScreenViewModel = hiltViewModel()
                val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

                MainScreen(
                    rootNavController = navController,
                    signOut = viewModel::signOut,
                    currentUser = currentUser
                )
            }
            composable(
                route = NavigationRoute.BulkAdd.route,
            ) {
                Log.e(
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
                route = "${NavigationRoute.Add.route}?id={id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) {
                Log.e(TAG, "NavGraphRoot: Add")
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
//            PreferencesScreen()
            }
            composable(
                route = NavigationRoute.Export.route
            ) {
//            ExportScreen()
            }
            composable(
                route = NavigationRoute.Restore.route
            ) {
//            RestoreScreen()
            }
            composable(
                route = NavigationRoute.Reset.route
            ) {
//            ResetScreen()
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
        inclusive = true
    }
    launchSingleTop = true
    restoreState = true
}

