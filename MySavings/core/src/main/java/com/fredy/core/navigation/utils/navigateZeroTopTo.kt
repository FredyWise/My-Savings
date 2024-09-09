package com.fredy.core.navigation.utils

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

fun NavHostController.navigateZeroTopTo(route: String) = this.navigate(
    route
) {
    popUpTo(this@navigateZeroTopTo.graph.findStartDestination().id) {
        inclusive = true
        saveState = true
    }
    restoreState = true
}

