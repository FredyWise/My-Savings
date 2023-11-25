package com.fredy.mysavings.ui.NavigationComponent.Navigation

import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.myapplication.ui.screens.authentication.SignIn
import com.example.myapplication.ui.screens.authentication.SignUp
import com.fredy.mysavings.ViewModels.Event.SignInEvent
import com.fredy.mysavings.ViewModels.SignInViewModel
import com.fredy.mysavings.ViewModels.SignUpViewModel

fun NavGraphBuilder.authenticationNavGraph(
    navController: NavHostController
) {
    navigation(
        route = Graph.Auth,
        startDestination = NavigationRoute.SignIn.route
    ) {
        composable(
            route = NavigationRoute.SignIn.route
        ) {
            val viewModel: SignInViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()
            val googleSignInState by viewModel.googleState
            val context = LocalContext.current

            viewModel.onEvent(SignInEvent.getSignedInUser)
            if (state.signedInUser != null) {
                navController.popBackStack()
                navController.navigate(
                    Graph.HomeNav
                )
            }

            LaunchedEffect(
                key1 = state.isError,
                key2 = googleSignInState.error
            ) {
                if (state.isError?.isNotEmpty() == true) {
                    viewModel.onEvent(SignInEvent.signOut)
                    val error = state.isError
                    Toast.makeText(
                        context,
                        "${error}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                if (googleSignInState.error.isNotEmpty()){
                    viewModel.onEvent(SignInEvent.signOut)
                    Toast.makeText(
                        context,
                        googleSignInState.error,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            LaunchedEffect(
                key1 = state.isSuccess,
                key2 = googleSignInState.success
            ) {
                if (state.isSuccess?.isNotEmpty() == true) {
                    val success = state.isSuccess
                    Toast.makeText(
                        context,
                        "${success}",
                        Toast.LENGTH_SHORT
                    ).show()
                    navController.popBackStack()
                    navController.navigate(
                        Graph.HomeNav
                    )
                }
                if (googleSignInState.success != null) {
                    Toast.makeText(
                        context,
                        "Sign In Success",
                        Toast.LENGTH_SHORT
                    ).show()
                    navController.popBackStack()
                    navController.navigate(
                        Graph.HomeNav
                    )
                }
            }
            SignIn(
                navController = navController,
                state = state,
                googleSignInState = googleSignInState,
                onEvent = viewModel::onEvent
            )
        }
        composable(
            route = NavigationRoute.SignUp.route
        ) {
            val viewModel: SignUpViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()
            val googleSignInState by viewModel.googleState
            val context = LocalContext.current
            LaunchedEffect(
                key1 = state.isError,
                key2 = googleSignInState.error
            ) {
                if (state.isError?.isNotEmpty() == true) {
                    val error = state.isError
                    Toast.makeText(
                        context,
                        "${error}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                if (googleSignInState.error.isNotEmpty()){
                    Toast.makeText(
                        context,
                        googleSignInState.error,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            LaunchedEffect(
                key1 = state.isSuccess,
                key2 = googleSignInState.success
            ) {
                if (state.isSuccess?.isNotEmpty() == true) {
                    val success = state.isSuccess
                    Toast.makeText(
                        context,
                        "$success",
                        Toast.LENGTH_LONG
                    ).show()
                    navController.navigateUp()
                }
                if (googleSignInState.success != null) {
                    Toast.makeText(
                        context,
                        "Sign Up Success",
                        Toast.LENGTH_SHORT
                    ).show()
                    navController.popBackStack()
                    navController.navigate(
                        Graph.HomeNav
                    )
                }
            }
            SignUp(
                navController = navController,
                state = state,
                googleSignInState = googleSignInState,
                onEvent = viewModel::onEvent
            )
        }
    }
}