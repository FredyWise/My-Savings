package com.fredy.mysavings.ui.NavigationComponent.Navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.AddToPhotos
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.AddToPhotos
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.HowToReg
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.ui.graphics.vector.ImageVector

object Graph {
    const val Root = "root"
    const val Auth = "auth"
    const val HomeNav = "home_nav"
    const val MainNav = "main_nav"
    const val FirstNav = "first"
}



val drawerScreens = listOf(
    NavigationRoute.Preferences,
    NavigationRoute.Export,
    NavigationRoute.Restore,
    NavigationRoute.Reset
)

val bottomBarScreens = listOf(
    NavigationRoute.Records,
    NavigationRoute.Analysis,
    NavigationRoute.Account,
    NavigationRoute.Categories,
)

sealed class NavigationRoute(
    val route: String,
    val title: String,
    val contentDescription: String,
    val icon: ImageVector,
    val iconNot: ImageVector,
) {
    //auth
    object SignIn: NavigationRoute(
        route = "signIn",
        title = "Sign In",
        contentDescription = "Go Sign In",
        icon = Icons.Default.Login,
        iconNot = Icons.Outlined.Login
    )

    object SignUp: NavigationRoute(
        route = "signUp",
        title = "Sign Up",
        contentDescription = "Go Sign Up",
        icon = Icons.Default.HowToReg,
        iconNot = Icons.Outlined.HowToReg
    )

    object ForgotPassword: NavigationRoute(
        route = "forgotPassword",
        title = "Forgot Password",
        contentDescription = "Go change password",
        icon = Icons.Default.Password,
        iconNot = Icons.Outlined.Password
    )

    //top bar
    object Preferences: NavigationRoute(
        route = "preferences",
        title = "Preferences",
        contentDescription = "Go to preferences screen",
        icon = Icons.Default.Settings,
        iconNot = Icons.Outlined.Settings
    )

    object Export: NavigationRoute(
        route = "export",
        title = "Export records",
        contentDescription = "Go to export screen",
        icon = Icons.Default.UploadFile,
        iconNot = Icons.Outlined.UploadFile
    )

    object Restore: NavigationRoute(
        route = "restore",
        title = "Backup & Restore",
        contentDescription = "Go to restore screen",
        icon = Icons.Default.Save,
        iconNot = Icons.Outlined.Save
    )

    object Reset: NavigationRoute(
        route = "reset",
        title = "Reset All Data",
        contentDescription = "Go to reset screen",
        icon = Icons.Default.Delete,
        iconNot = Icons.Outlined.Delete
    )

    //bottom bar
    object Records: NavigationRoute(
        route = "records",
        title = "Records",
        contentDescription = "Go to Records Screen",
        icon = Icons.Default.Receipt,
        iconNot = Icons.Outlined.Receipt
    )

    object Categories: NavigationRoute(
        route = "categories",
        title = "Categories",
        contentDescription = "Go to Categories Screen",
        icon = Icons.Default.Label,
        iconNot = Icons.Outlined.Label
    )

    object Account: NavigationRoute(
        route = "account",
        title = "Account",
        contentDescription = "Go to Account Screen",
        icon = Icons.Default.AccountBalanceWallet,
        iconNot = Icons.Outlined.AccountBalanceWallet
    )

    object Analysis: NavigationRoute(
        route = "analysis",
        title = "Analysis",
        contentDescription = "Go to Analysis Screen",
        icon = Icons.Default.PieChart,
        iconNot = Icons.Outlined.PieChart
    )

    //other screen
    object Add: NavigationRoute(
        route = "add",
        title = "Add",
        contentDescription = "Go to Add Screen",
        icon = Icons.Default.AddBox,
        iconNot = Icons.Outlined.AddBox
    )
    object BulkAdd: NavigationRoute(
        route = "addBulk",
        title = "AddBulk",
        contentDescription = "Go to Bulk Add Screen",
        icon = Icons.Default.AddToPhotos,
        iconNot = Icons.Outlined.AddToPhotos
    )
}
//Icons.Default.CenterFocusStrong