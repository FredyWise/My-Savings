package com.fredy.analysis.ui


import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fredy.analysis.AnalysisNavGraph
import com.fredy.analysis.AnalysisNavigationRoute
import com.fredy.analysis.analysisScreens
import com.fredy.analysis.viewModel.AnalysisEvent
import com.fredy.analysis.viewModel.AnalysisState
import com.fredy.domain.enumsChecker.recordTypeColor
import com.fredy.domain.model.Category
import com.fredy.domain.model.Wallet
import com.fredy.ui.util.navigation.navigateSingleTopTo


@Composable
fun AnalysisScreen(
    modifier: Modifier = Modifier,
    rootNavController: NavHostController,
    state: AnalysisState,
    onEvent: (AnalysisEvent) -> Unit,
    onGetCategoryDetails: (Category) -> Unit,
    onGetWalletDetails: (Wallet) -> Unit,
) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentScreen =
        analysisScreens.find { it.route == currentDestination?.route } ?: AnalysisNavigationRoute.Analysis
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        AnalysisTabRow(
            allScreens = analysisScreens,
            color = state.filterState.recordType.recordTypeColor().copy(alpha = 0.8f),
            onTabSelected = { screen ->
                navController.navigateSingleTopTo(
                    screen.route
                )
                if (currentScreen == screen && screen != AnalysisNavigationRoute.AnalysisWallet) {
                    onEvent(
                        AnalysisEvent.ToggleAnalysisType
                    )
                }
            },
            currentScreen = currentScreen,
        )
        AnalysisNavGraph(
            rootNavController = rootNavController,
            navController = navController,
            state = state,
            onEvent = onEvent,
            onGetCategoryDetails = onGetCategoryDetails,
            onGetWalletDetails = onGetWalletDetails,
        )

    }
}

