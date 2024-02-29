package com.fredy.mysavings.ui.NavigationComponent.Navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.AddToPhotos
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.DataSaverOff
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.LineAxis
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.AddToPhotos
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CurrencyExchange
import androidx.compose.material.icons.outlined.DataSaverOff
import androidx.compose.material.icons.outlined.HowToReg
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.LineAxis
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.ui.graphics.vector.ImageVector

object Graph {
    const val RootNav = "root_nav"
    const val AuthNav = "auth_nav"
    const val HomeNav = "home_nav"
    const val MainNav = "main_nav"
    const val AnalysisNav = "analysis_nav"
}

val analysisScreens = listOf(
    NavigationRoute.AnalysisOverview,
    NavigationRoute.AnalysisFlow,
    NavigationRoute.AnalysisAccount,
)

val drawerScreens = listOf(
    NavigationRoute.Preferences,
    NavigationRoute.Export,
    NavigationRoute.Currency
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
    object Profile: NavigationRoute(
        route = "profile",
        title = "Profile",
        contentDescription = "Go to profile screen",
        icon = Icons.Default.Settings,
        iconNot = Icons.Outlined.Settings
    )

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

    object Currency: NavigationRoute(
        route = "currency",
        title = "Currency",
        contentDescription = "Go to currency screen",
        icon = Icons.Default.CurrencyExchange,
        iconNot = Icons.Outlined.CurrencyExchange
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
        route = "wallet",
        title = "Wallet",
        contentDescription = "Go to Wallet Screen",
        icon = Icons.Default.AccountBalanceWallet,
        iconNot = Icons.Outlined.AccountBalanceWallet
    )

    // Analysis
    object Analysis: NavigationRoute(
        route = "analytics",
        title = "Analytics",
        contentDescription = "Go to Analytics Screen",
        icon = Icons.Default.PieChart,
        iconNot = Icons.Outlined.PieChart
    )
    object AnalysisOverview: NavigationRoute(
        route = "analytics Overview",
        title = "Overview",
        contentDescription = "Go to Overview Screen",
        icon = Icons.Default.DataSaverOff,
        iconNot = Icons.Outlined.DataSaverOff
    )

    object AnalysisFlow: NavigationRoute(
        route = "analytics Flow",
        title = "Flow",
        contentDescription = "Go to Flow Screen",
        icon = Icons.Default.LineAxis,
        iconNot = Icons.Outlined.LineAxis
    )

    object AnalysisAccount: NavigationRoute(
        route = "analytics Account",
        title = "Account",
        contentDescription = "Go to Account Screen",
        icon = Icons.Default.BarChart,
        iconNot = Icons.Outlined.BarChart
    )

    //other screen
    object Search: NavigationRoute(
        route = "search",
        title = "Search",
        contentDescription = "Go to Search Screen",
        icon = Icons.Default.Search,
        iconNot = Icons.Outlined.Search
    )
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