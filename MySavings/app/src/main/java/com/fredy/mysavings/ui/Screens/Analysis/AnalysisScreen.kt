package com.fredy.mysavings.ui.Screens.Analysis


import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fredy.mysavings.ViewModels.Event.RecordsEvent
import com.fredy.mysavings.ViewModels.RecordState
import com.fredy.mysavings.ui.NavigationComponent.Navigation.AnalysisNavGraph
import com.fredy.mysavings.ui.NavigationComponent.Navigation.NavigationRoute
import com.fredy.mysavings.ui.NavigationComponent.Navigation.analysisScreens
import com.fredy.mysavings.ui.NavigationComponent.Navigation.navigateSingleTopTo

@Composable
fun AnalysisScreen(
    modifier: Modifier = Modifier,
    rootNavController: NavHostController,
    state: RecordState,
    onEvent: (RecordsEvent) -> Unit,
) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentScreen =
        analysisScreens.find { it.route == currentDestination?.route } ?: NavigationRoute.Records
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AnalysisTabRow(
            allScreens = analysisScreens,
            onTabSelected = { screen ->
                navController.navigateSingleTopTo(
                    screen.route
                )
                if (currentScreen == screen && screen != NavigationRoute.AnalysisAccount) {
                    onEvent(
                        RecordsEvent.ToggleRecordType
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
        )

    }
}

