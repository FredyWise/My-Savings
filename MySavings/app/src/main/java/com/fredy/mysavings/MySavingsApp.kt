package com.fredy.mysavings

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fredy.mysavings.ui.bottomBar.BottomBar
import com.fredy.mysavings.ui.navigation.BottomNavGraph
import com.fredy.mysavings.ui.navigation.NavGraphRoot
import com.fredy.mysavings.ui.navigation.NavigationRoute
import com.fredy.mysavings.ui.navigation.bottomBarScreens
import com.fredy.mysavings.ui.navigation.navigateSingleTopTo
import com.fredy.mysavings.ui.theme.MySavingsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySavingsApp(
) {
    MySavingsTheme {
        val navController = rememberNavController()
        NavGraphRoot(navController)
    }
}