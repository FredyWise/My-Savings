package com.fredy.mysavings.ui.NavigationComponent.Navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fredy.mysavings.ViewModels.AnalysisState
import com.fredy.mysavings.ViewModels.Event.AnalysisEvent
import com.fredy.mysavings.ui.Screens.Analysis.AnalysisAccount
import com.fredy.mysavings.ui.Screens.Analysis.AnalysisFlow
import com.fredy.mysavings.ui.Screens.Analysis.AnalysisOverview


@Composable
fun AnalysisNavGraph(
    rootNavController: NavHostController,
    navController: NavHostController,
    state: AnalysisState,
    onEvent: (AnalysisEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.AnalysisOverview.route,
        modifier = modifier
    ) {
        composable(
            route = NavigationRoute.AnalysisOverview.route
        ) {
            AnalysisOverview(
                state = state, onEvent = onEvent
            )
        }
        composable(
            route = NavigationRoute.AnalysisFlow.route
        ) {
            AnalysisFlow(
                state = state, onEvent = onEvent
            )
        }
        composable(
            route = NavigationRoute.AnalysisAccount.route
        ) {
            AnalysisAccount(
                state = state, onEvent = onEvent
            )
        }
    }
}
