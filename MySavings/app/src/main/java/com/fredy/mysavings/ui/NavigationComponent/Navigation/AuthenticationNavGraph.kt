package com.fredy.mysavings.ui.NavigationComponent.Navigation

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.TAG
import com.fredy.mysavings.ViewModels.AuthViewModel
import com.fredy.mysavings.ViewModels.Event.AuthEvent
import com.fredy.mysavings.ViewModels.SettingViewModel
import com.fredy.mysavings.ui.Screens.AuthScreen.SignIn
import com.fredy.mysavings.ui.Screens.AuthScreen.SignUp

fun NavGraphBuilder.authenticationNavGraph(
    settingViewModel: SettingViewModel,
    viewModel: AuthViewModel,
    rootNavController: NavHostController
) {
    navigation(
        route = Graph.AuthNav,
        startDestination = NavigationRoute.SignIn.route,
        enterTransition = {
            fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300))
        },
    ) {
        composable(
            route = NavigationRoute.SignIn.route,
            enterTransition = {
                fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300))
            },
        ) {
            val setting by settingViewModel.state.collectAsStateWithLifecycle()
            val state by viewModel.state.collectAsStateWithLifecycle()
            val context = LocalContext.current
            LaunchedEffect(
                key1 = state.bioAuthResource,
            ) {
                when (state.bioAuthResource) {
                    is Resource.Error -> {
                        viewModel.onEvent(
                            AuthEvent.SignOut
                        )
                        val error = state.bioAuthResource.message
                        Toast.makeText(
                            context,
                            "${error}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    is Resource.Success -> {
                        Toast.makeText(
                            context,
                            state.bioAuthResource.data,
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.onEvent(AuthEvent.GetCurrentUser)
                        rootNavController.navigate(
                            Graph.MainNav
                        )
                    }

                    else -> {
                    }
                }
            }
            LaunchedEffect(
                key1 = state.authResource,
            ) {
                when (state.authResource) {
                    is Resource.Error -> {
                        viewModel.onEvent(
                            AuthEvent.SignOut
                        )
                        val error = state.authResource.message
                        Toast.makeText(
                            context,
                            "${error}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    is Resource.Success -> {
                        Toast.makeText(
                            context,
                            "SignIn Success",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.onEvent(AuthEvent.GetCurrentUser)
                        rootNavController.navigate(
                            Graph.MainNav
                        )
                    }

                    else -> {
                    }
                }
            }

            SignIn(
                navController = rootNavController,
                state = state,
                isUsingBioAuth = setting.bioAuth,
                onEvent = viewModel::onEvent
            )
        }
        composable(
            route = NavigationRoute.SignUp.route,
            enterTransition = {
                fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300))
            },
        ) {
            val state by viewModel.state.collectAsStateWithLifecycle()
            val context = LocalContext.current

            LaunchedEffect(
                key1 = state.authResource,
            ) {
                when (state.authResource) {
                    is Resource.Error -> {
                        viewModel.onEvent(
                            AuthEvent.SignOut
                        )
                        val error = state.authResource.message
                        Toast.makeText(
                            context,
                            "${error}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    is Resource.Success -> {
                        Toast.makeText(
                            context,
                            "SignIn Success",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.onEvent(AuthEvent.GetCurrentUser)
                        rootNavController.navigate(
                            Graph.MainNav
                        )
                    }

                    else -> {
                    }
                }
            }
            SignUp(
                navController = rootNavController,
                state = state,
                onEvent = viewModel::onEvent
            )
        }
    }
}