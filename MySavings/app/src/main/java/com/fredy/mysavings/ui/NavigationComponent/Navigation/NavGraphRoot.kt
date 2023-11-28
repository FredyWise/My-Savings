package com.fredy.mysavings.ui.NavigationComponent.Navigation

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.fredy.mysavings.Util.TAG
import com.fredy.mysavings.ViewModels.AuthViewModel
import com.fredy.mysavings.ViewModels.Event.AuthEvent
import com.fredy.mysavings.ViewModels.MainScreenViewModel
import com.fredy.mysavings.ui.Screens.AddRecord.AddScreen
import com.fredy.mysavings.ui.NavigationComponent.MainScreen

@Composable
fun NavGraphRoot(
    navController: NavHostController,
) {
    NavHost(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        navController = navController,
        startDestination = Graph.FirstNav,
        route = Graph.Root
    ) {
        composable(
            route = Graph.FirstNav
        ){
            Log.e(TAG, "NavGraphRoot: ", )
            val viewModel: AuthViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()
            val startDestination = if (state.signedInUser != null) Graph.HomeNav else Graph.Auth
            Log.e(TAG, "NavGraphRoot: ", )
            navController.navigate(startDestination)
        }
        authenticationNavGraph(navController = navController)
        navigation(
            route = Graph.MainNav,
            startDestination = Graph.HomeNav,
        ) {
            composable(
                route = Graph.HomeNav
            ) {
                val viewModel: MainScreenViewModel = hiltViewModel()
                val currentUser = viewModel.currentUser.collectAsState().value

                MainScreen(
                    rootNavController = navController,
                    signOut = viewModel::signOut,
                    currentUser = currentUser
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
            composable(
                route = "${NavigationRoute.Add.route}?id={id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) {
                val id = it.arguments?.getString("id") ?: "-1"
                AddScreen(modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.background
                    )
                    .padding(8.dp),
                    id = id,
                    navigateUp = { navController.navigateUp() })
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