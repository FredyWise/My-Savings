package com.fredy.mysavings.ui.NavigationComponent

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fredy.mysavings.Data.Database.Entity.UserData
import com.fredy.mysavings.ui.NavigationComponent.Navigation.Graph
import com.fredy.mysavings.ui.NavigationComponent.Navigation.HomeNavGraph
import com.fredy.mysavings.ui.NavigationComponent.Navigation.NavigationRoute
import com.fredy.mysavings.ui.NavigationComponent.Navigation.bottomBarScreens
import com.fredy.mysavings.ui.NavigationComponent.Navigation.drawerScreens
import com.fredy.mysavings.ui.NavigationComponent.Navigation.navigateSingleTopTo
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = MaterialTheme.colorScheme.surface,
    onContentColor: Color = MaterialTheme.colorScheme.onSurface,
    rootNavController: NavHostController,
    currentUser: UserData,
    signOut: () -> Unit,
) {
    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentScreen = bottomBarScreens.find { it.route == currentDestination?.route } ?: NavigationRoute.Records
    Scaffold(
        modifier = modifier,
        backgroundColor = backgroundColor,
        scaffoldState = scaffoldState,
        topBar = {
            AppBar(
                backgroundColor = contentColor,
                contentColor = onContentColor,
                onNavigationIconClick = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                },
                currentUser = currentUser
            )
        },
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
        drawerBackgroundColor = contentColor,
        drawerContentColor = onContentColor,
        drawerContent = {
            DrawerHeader()
            DrawerBody(items = drawerScreens,
                onItemClick = { newScreen ->
                    rootNavController.navigateSingleTopTo(
                        newScreen.route
                    )
                },
                additionalItem = {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            signOut()
                            Toast
                                .makeText(
                                    context,
                                    "Signed out",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                            rootNavController.navigateSingleTopTo(Graph.Auth)
                        }
                        .padding(16.dp)) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(
                            modifier = Modifier.width(
                                16.dp
                            )
                        )
                        Text(
                            text = "Sign Out",
                            style = TextStyle(
                                fontSize = 18.sp
                            ),
                            modifier = Modifier.weight(
                                1f
                            )
                        )
                    }
                })
        },
        bottomBar = {
            BottomBar(
                modifier = Modifier.height(
                    65.dp
                ),
                backgroundColor = contentColor,
                contentColor = onContentColor,
                allScreens = bottomBarScreens,
                onTabSelected = { newScreen ->
                    navController.navigateSingleTopTo(
                        newScreen.route
                    )
                },
                currentScreen = currentScreen
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    rootNavController.navigate(
                        NavigationRoute.Add.route + "?id=-1"
                    )
                },
                backgroundColor = contentColor,
            ) {
                Icon(
                    NavigationRoute.Add.icon,
                    modifier = Modifier.size(30.dp),
                    tint = onContentColor,
                    contentDescription = ""
                )
            }
        },

        ) { innerPadding ->
        HomeNavGraph(
            rootNavController = rootNavController,
            navController = navController,
            modifier = Modifier.padding(
                innerPadding
            ),
        )
    }
}