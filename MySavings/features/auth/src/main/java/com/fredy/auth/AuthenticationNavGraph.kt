package com.fredy.auth

import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.fredy.auth.ui.SignIn

import com.fredy.auth.viewModel.AuthEvent
import com.fredy.auth.viewModel.AuthViewModel
import com.fredy.core.navigation.Graph
import com.fredy.core.navigation.utils.navigateZeroTopTo
import com.fredy.domain.util.resource.Resource

fun NavGraphBuilder.authenticationNavGraph(
    isUsingBioAuth: Boolean,
    viewModel: AuthViewModel,
    rootNavController: NavHostController
) {
    navigation(
        route = Graph.AuthNav,
        startDestination = AuthNavigationRoute.SignIn.route,
    ) {
        composable(
            route = AuthNavigationRoute.SignIn.route,
        ) {
            val state by viewModel.state.collectAsStateWithLifecycle()
            val context = LocalContext.current
            val authResource = state.authResource
            LaunchedEffect(
                key1 = authResource,
            ) {
                when (authResource) {
                    is Resource.Error -> {
                        viewModel.onEvent(
                            AuthEvent.SignOut
                        )
                        Toast.makeText(
                            context,
                            authResource.error.name,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    is Resource.Success -> {
                        Toast.makeText(
                            context,
                            authResource.data,
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.onEvent(
                            AuthEvent.GetCurrentUser
                        )
                        rootNavController.navigateZeroTopTo(
                            Graph.MainNav
                        )
                    }

                    else -> {
                    }
                }
            }

            SignIn(
                state = state,
                isUsingBioAuth = isUsingBioAuth,
                onEvent = viewModel::onEvent,
                navigateToSignUp = {
                    rootNavController.navigateZeroTopTo(
                        AuthNavigationRoute.SignUp.route
                    )
                }
            )


        }

    }
}