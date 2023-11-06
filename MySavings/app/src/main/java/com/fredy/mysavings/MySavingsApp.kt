package com.fredy.mysavings

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.fredy.mysavings.ui.navigation.NavGraphRoot
import com.fredy.mysavings.ui.theme.MySavingsTheme

@Composable
fun MySavingsApp(
) {
    MySavingsTheme {
        val navController = rememberNavController()
        NavGraphRoot(navController)
    }
}