package com.fredy.mysavings.ui.NavigationComponent

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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

@OptIn(ExperimentalMaterialApi::class)
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
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var isFabVisible by remember {
        mutableStateOf(
            true
        )
    }
    var isShowingAdd by remember {
        mutableStateOf(
            false
        )
    }
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
                            rootNavController.navigateSingleTopTo(
                                Graph.Auth
                            )
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
                    isShowingAdd = false
                },
                currentScreen = currentScreen
            )
        },
        floatingActionButton = {
            AnimatedVisibility(visible = isFabVisible) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    AnimatedVisibility(visible = isShowingAdd) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(
                                        CircleShape
                                    )
                                    .clickable {
                                        rootNavController.navigate(
                                            NavigationRoute.Add.route + "?id=-1"
                                        )
                                    }
                                    .background(
                                        contentColor
                                    )
                                    .padding(8.dp),
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
                            Box(
                                modifier = Modifier
                                    .clip(
                                        CircleShape
                                    )
                                    .clickable {
                                        rootNavController.navigate(
                                            NavigationRoute.BulkAdd.route
                                        )
                                    }
                                    .background(
                                        contentColor
                                    )
                                    .padding(8.dp),
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
            }
        },

        ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .pointerInput(
                Unit
            ) {
                detectTransformGestures { _, panGesture, _, _ ->
                    offsetX += panGesture.x
                    offsetY += panGesture.y
                    if (panGesture.y < -size.height / 30) {
                        isFabVisible = false
                    } else if (panGesture.y > size.height / 30) {
                        isFabVisible = true
                    }
                }
            }) {
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