package com.fredy.analysis;

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DataSaverOff
import androidx.compose.material.icons.filled.LineAxis
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.DataSaverOff
import androidx.compose.material.icons.outlined.LineAxis
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.ui.graphics.vector.ImageVector


val analysisScreens = listOf(
    AnalysisNavigationRoute.AnalysisOverview,
    AnalysisNavigationRoute.AnalysisFlow,
    AnalysisNavigationRoute.AnalysisWallet,
)

sealed class AnalysisNavigationRoute(
    val route: String,
    val title: String,
    val contentDescription: String,
    val icon: ImageVector,
    val iconNot: ImageVector,
) {
    object Analysis : AnalysisNavigationRoute(
        route = "analytics",
        title = "Analytics",
        contentDescription = "Go to Analytics Screen",
        icon = Icons.Default.PieChart,
        iconNot = Icons.Outlined.PieChart
    )

    object AnalysisOverview : AnalysisNavigationRoute(
        route = "analytics Overview",
        title = "Overview",
        contentDescription = "Go to Overview Screen",
        icon = Icons.Default.DataSaverOff,
        iconNot = Icons.Outlined.DataSaverOff
    )

    object AnalysisFlow : AnalysisNavigationRoute(
        route = "analytics Flow",
        title = "Flow",
        contentDescription = "Go to Flow Screen",
        icon = Icons.Default.LineAxis,
        iconNot = Icons.Outlined.LineAxis
    )

    object AnalysisWallet : AnalysisNavigationRoute(
        route = "analytics Wallet",
        title = "Wallet",
        contentDescription = "Go to Wallet Screen",
        icon = Icons.Default.BarChart,
        iconNot = Icons.Outlined.BarChart
    )
}