package com.fredy.mysavings.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.PieChartOutline
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.ui.graphics.vector.ImageVector

object Graph {
    const val Root = "root"
    const val Auth = "auth"
    const val BottomNav = "bottom_nav"
}

sealed class AuthenticationRoute(val route: String) {
    object SignIn: AuthenticationRoute(route = "signIn")
    object SignUp: AuthenticationRoute(route = "signUp")
    object ForgotPassword: AuthenticationRoute(
        route = "forgotPassword"
    )
}

val bottomBarScreens = listOf(
    NavigationRoute.Records,
    NavigationRoute.Analysis,
    NavigationRoute.Account,
    NavigationRoute.Categories,
)

sealed class NavigationRoute(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val iconNot: ImageVector,
) {
    object Records: NavigationRoute(
        route = "records",
        title = "Records",
        icon = Icons.Default.Receipt,
        iconNot = Icons.Outlined.Receipt
    )

    object Categories: NavigationRoute(
        route = "categories",
        title = "Categories",
        icon = Icons.Default.Label,
        iconNot = Icons.Outlined.Label
    )

    object Account: NavigationRoute(
        route = "account",
        title = "Account",
        icon = Icons.Default.AccountBalanceWallet,
        iconNot = Icons.Outlined.AccountBalanceWallet
    )

    object Analysis: NavigationRoute(
        route = "analysis",
        title = "Analysis",
        icon = Icons.Default.PieChart,
        iconNot = Icons.Outlined.PieChart
    )

    object Add: NavigationRoute(
        route = "add",
        title = "Add",
        icon = Icons.Default.Add,
        iconNot = Icons.Outlined.Add
    )
}
//Icons.Default.CenterFocusStrong