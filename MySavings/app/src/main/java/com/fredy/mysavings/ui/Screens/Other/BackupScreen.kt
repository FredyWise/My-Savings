package com.fredy.mysavings.ui.Screens.Other

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.fredy.mysavings.ui.Screens.ZCommonComponent.DefaultAppBar

@Composable
fun BackupScreen(
    modifier: Modifier = Modifier,
    rootNavController: NavController,
    title: String,
) {
    DefaultAppBar(
        modifier = modifier, title = title,
        onNavigationIconClick = { rootNavController.navigateUp() },
    ) {

    }
}