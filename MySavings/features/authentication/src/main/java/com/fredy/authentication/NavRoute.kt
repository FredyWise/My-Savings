package com.fredy.authentication

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.outlined.HowToReg
import androidx.compose.ui.graphics.vector.ImageVector


sealed class AuthNavigationRoute(
    val route: String,
    val title: String,
    val contentDescription: String,
    val icon: ImageVector,
    val iconNot: ImageVector,
) {
    //auth
    object SignIn: AuthNavigationRoute(
        route = "signIn",
        title = "Sign In",
        contentDescription = "Go Sign In",
        icon = Icons.AutoMirrored.Filled.Login,
        iconNot = Icons.AutoMirrored.Outlined.Login
    )

    object SignUp: AuthNavigationRoute(
        route = "signUp",
        title = "Sign Up",
        contentDescription = "Go Sign Up",
        icon = Icons.Default.HowToReg,
        iconNot = Icons.Outlined.HowToReg
    )

}