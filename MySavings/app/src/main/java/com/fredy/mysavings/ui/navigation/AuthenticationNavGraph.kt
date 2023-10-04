package com.fredy.mysavings.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation

fun NavGraphBuilder.authenticationNavGraph(
    navController: NavHostController
) {
    navigation(
        route = Graph.Auth,
        startDestination = AuthenticationRoute.SignIn.route
    ) {
        composable(
            route = AuthenticationRoute.SignIn.route
        ) {
//            SignIn(
//                gotoHome = {
//                    navController.popBackStack()
//                    navController.popBackStack()
//                    navController.navigate(Graph.Home)
//                },
//                gotoSignUp = {
//                    navController.navigate(AuthenticationRoute.SignUp.route)
//                },
//                gotoForgotPassword = {
//                    navController.navigate(AuthenticationRoute.ForgotPassword.route)
//                }
//            )
        }
        composable(
            route = AuthenticationRoute.SignUp.route
        ) {
//            SignUp(gotoSignIn = {
//                navController.popBackStack()
//                navController.popBackStack()
//                navController.navigate(AuthenticationRoute.SignIn.route)
//            })
        }
        composable(
            route = AuthenticationRoute.ForgotPassword.route
        ) {
//            ForgotPassword(gotoSignIn = {
//                navController.popBackStack()
//                navController.popBackStack()
//                navController.navigate(AuthenticationRoute.SignIn.route)
//            })
        }
    }
}