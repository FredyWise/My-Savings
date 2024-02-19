package com.fredy.mysavings.ui.NavigationComponent.Navigation

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.fredy.mysavings.Util.TAG
import com.fredy.mysavings.ViewModels.AuthViewModel
import com.fredy.mysavings.ViewModels.Event.AuthEvent
import com.fredy.mysavings.ViewModels.RecordViewModel
import com.fredy.mysavings.ViewModels.SearchViewModel
import com.fredy.mysavings.ViewModels.SettingViewModel
import com.fredy.mysavings.ui.NavigationComponent.MainScreen
import com.fredy.mysavings.ui.Screens.AddBulk.BulkAddScreen
import com.fredy.mysavings.ui.Screens.AddSingle.AddScreen
import com.fredy.mysavings.ui.Screens.Other.ExportScreen
import com.fredy.mysavings.ui.Screens.Other.PreferencesScreen
import com.fredy.mysavings.ui.Screens.Other.ProfileScreen
import com.fredy.mysavings.ui.Search.SearchScreen

@Composable
fun NavGraphRoot(
    navController: NavHostController,
    settingViewModel: SettingViewModel,
    authViewModel: AuthViewModel,
    startDestination: String
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
            ) {
                val state by authViewModel.state.collectAsStateWithLifecycle()
                val context = LocalContext.current
                MainScreen(
                    rootNavController = navController,
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
                enterTransition = {
                    fadeIn()
                },
                exitTransition = {
                    fadeOut()
                },
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
                val state by settingViewModel.state.collectAsStateWithLifecycle()
                ExportScreen(
                    title = NavigationRoute.Export.title,
                    rootNavController = navController,
                    state = state,
                    onEvent = settingViewModel::onEvent
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
        this@navigateSingleTopTo.graph.findStartDestination().id
    ) {
        saveState = true
    }
    launchSingleTop = true
    restoreState = true

}

