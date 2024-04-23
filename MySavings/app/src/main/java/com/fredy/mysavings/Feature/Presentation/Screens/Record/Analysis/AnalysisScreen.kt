package com.fredy.mysavings.Feature.Presentation.Screens.Record.Analysis


import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fredy.mysavings.Util.RecordTypeColor
import com.fredy.mysavings.Feature.Presentation.ViewModels.WalletViewModel.WalletState
import com.fredy.mysavings.Feature.Presentation.ViewModels.CategoryViewModel.CategoryState
import com.fredy.mysavings.Feature.Presentation.ViewModels.WalletViewModel.WalletEvent
import com.fredy.mysavings.Feature.Presentation.ViewModels.CategoryViewModel.CategoryEvent
import com.fredy.mysavings.Feature.Presentation.ViewModels.RecordViewModel.RecordEvent
import com.fredy.mysavings.Feature.Presentation.ViewModels.RecordViewModel.RecordState
import com.fredy.mysavings.Feature.Presentation.NavigationComponent.Navigation.AnalysisNavGraph
import com.fredy.mysavings.Feature.Presentation.NavigationComponent.Navigation.NavigationRoute
import com.fredy.mysavings.Feature.Presentation.NavigationComponent.Navigation.analysisScreens
import com.fredy.mysavings.Feature.Presentation.NavigationComponent.Navigation.navigateSingleTopTo

@Composable
fun AnalysisScreen(
    modifier: Modifier = Modifier,
    rootNavController: NavHostController,
    state: RecordState,
    onEvent: (RecordEvent) -> Unit,
    categoryState: CategoryState,
    categoryEvent: (CategoryEvent) -> Unit,
    walletState: WalletState,
    accountEvent: (WalletEvent) -> Unit,
) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentScreen =
        analysisScreens.find { it.route == currentDestination?.route } ?: NavigationRoute.Records
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        AnalysisTabRow(
            allScreens = analysisScreens,
            color = RecordTypeColor(recordType = state.filterState.recordType).copy(alpha = 0.8f),
            onTabSelected = { screen ->
                navController.navigateSingleTopTo(
                    screen.route
                )
                if (currentScreen == screen && screen != NavigationRoute.AnalysisWallet) {
                    onEvent(
                        RecordEvent.ToggleRecordType
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
            categoryState = categoryState,
            categoryEvent = categoryEvent,
            walletState = walletState,
            accountEvent = accountEvent
        )

    }
}

