package com.fredy.mysavings.ui.NavigationComponent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fredy.mysavings.Data.Database.Model.UserData
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
    currentUser: UserData?,
    signOut: () -> Unit,
) {
    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    var offsetX by remember { mutableStateOf(0f) }/*
    var offsetY by remember { mutableStateOf(0f) }
    var isFabVisible by remember {
        mutableStateOf(
            true
        )
    }*/
    var isShowingAdd by remember {
        mutableStateOf(
            false
        )
    }
    val scope = rememberCoroutineScope()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentScreen =
        bottomBarScreens.find { it.route == currentDestination?.route } ?: NavigationRoute.Records
    Scaffold(
        modifier = modifier,
        backgroundColor = backgroundColor,
        scaffoldState = scaffoldState,
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
        drawerBackgroundColor = contentColor,
        drawerContentColor = onContentColor,
        drawerContent = {
            DrawerHeader()
            DrawerBody(
                items = drawerScreens,
                onItemClick = { newScreen ->
                    rootNavController.navigate(
                        newScreen.route
                    )
                },
                additionalItem = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                signOut()
                            }
                            .padding(16.dp),
                    ) {
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
                },
            )
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
                    isShowingAdd = false
                },
                currentScreen = currentScreen
            )
        },
        floatingActionButton = {
//            AnimatedVisibility(visible = isFabVisible) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AnimatedVisibility(
                    visible = isShowingAdd,
                    enter = fadeIn(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300)),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        FloatingActionButton(
                            onClick = {
                                rootNavController.navigate(
                                    NavigationRoute.Add.route + "/-1"
                                )
                            },
                            backgroundColor = contentColor,
                            modifier = Modifier.border(
                                1.dp,
                                MaterialTheme.colorScheme.secondary.copy(
                                    0.3f
                                ),
                                CircleShape
                            ),
                        ) {
                            Icon(
                                NavigationRoute.Add.icon,
                                modifier = Modifier.size(
                                    30.dp
                                ),
                                tint = onContentColor,
                                contentDescription = ""
                            )
                        }
                        Spacer(
                            modifier = Modifier.height(
                                8.dp
                            )
                        )
                        FloatingActionButton(
                            onClick = {
                                rootNavController.navigate(
                                    NavigationRoute.BulkAdd.route
                                )
                            },
                            backgroundColor = contentColor,
                            modifier = Modifier.border(
                                1.dp,
                                MaterialTheme.colorScheme.secondary.copy(
                                    0.3f
                                ),
                                CircleShape
                            ),
                        ) {
                            Icon(
                                NavigationRoute.BulkAdd.icon,
                                modifier = Modifier.size(
                                    30.dp
                                ),
                                tint = onContentColor,
                                contentDescription = ""
                            )
                        }
                        Spacer(
                            modifier = Modifier.height(
                                8.dp
                            )
                        )
                    }
                }
                FloatingActionButton(
                    onClick = {
                        isShowingAdd = !isShowingAdd
                    },
                    backgroundColor = contentColor,
                    modifier = Modifier.border(
                        1.dp,
                        MaterialTheme.colorScheme.secondary.copy(
                            0.3f
                        ),
                        CircleShape
                    ),
                ) {
                    Icon(
                        Icons.Default.Add,
                        modifier = Modifier.size(
                            35.dp
                        ),
                        tint = onContentColor,
                        contentDescription = ""
                    )
                }
            }
//            }
        },

        ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
//                .pointerInput(
//                    Unit
//                ) {
//                    detectVerticalDragGestures()
//                },
        ) {
            AppBar(
                backgroundColor = contentColor,
                contentColor = onContentColor,
                onNavigationIconClick = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                },
                onProfilePictureClick = {
                    rootNavController.navigate(
                        NavigationRoute.Profile.route
                    )
                },
                currentUser = currentUser
            )
            HomeNavGraph(
                rootNavController = rootNavController,
                navController = navController,
                modifier = Modifier.padding(
                    innerPadding
                ),
            )
        }
    }
}