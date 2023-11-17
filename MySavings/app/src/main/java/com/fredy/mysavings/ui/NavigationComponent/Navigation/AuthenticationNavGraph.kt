package com.fredy.mysavings.ui.NavigationComponent.Navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.fredy.mysavings.ui.Screens.MainScreen

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
//            SignIn(
//                gotoHome = {
//                    navController.popBackStack()
//                    navController.popBackStack()
//                    navController.navigate(Graph.Home)
//                },
//                gotoSignUp = {
//                    navController.navigate(NavigationRoute.SignUp.route)
//                },
//                gotoForgotPassword = {
//                    navController.navigate(NavigationRoute.ForgotPassword.route)
//                }
//            )
        }
        composable(
            route = NavigationRoute.SignUp.route
        ) {
//            SignUp(gotoSignIn = {
//                navController.popBackStack()
//                navController.popBackStack()
//                navController.navigate(NavigationRoute.SignIn.route)
//            })
        }
        composable(
            route = NavigationRoute.ForgotPassword.route
        ) {
//            ForgotPassword(gotoSignIn = {
//                navController.popBackStack()
//                navController.popBackStack()
//                navController.navigate(NavigationRoute.SignIn.route)
//            })
        }
    }
}