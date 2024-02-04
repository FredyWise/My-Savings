package com.fredy.mysavings.ui.Screens.Other

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.fredy.mysavings.ui.Screens.ZCommonComponent.CustomStickyHeader
import com.fredy.mysavings.ui.Screens.ZCommonComponent.DefaultAppBar

@Composable
fun PreferencesScreen(
    modifier: Modifier = Modifier,
    rootNavController: NavController,
    title: String,
    headerColor: Color = MaterialTheme.colorScheme.primary.copy(0.8f)
) {
    DefaultAppBar(
        modifier = modifier,
        title = title,
        onNavigationIconClick = { rootNavController.navigateUp() },
    ) {
        CustomStickyHeader(
            textStyle = MaterialTheme.typography.titleLarge,
            title = "Appearance",
            textColor = headerColor,
            useDivider = false
        )
        CustomStickyHeader(
            textStyle = MaterialTheme.typography.titleLarge,
            title = "Security",
            textColor = headerColor,
            useDivider = false
        )
        CustomStickyHeader(
            textStyle = MaterialTheme.typography.titleLarge,
            title = "Notification",
            textColor = headerColor,
            useDivider = false
        )
        CustomStickyHeader(
            textStyle = MaterialTheme.typography.titleLarge,
            title = "About",
            textColor = headerColor,
            useDivider = false
        )
    }
}
