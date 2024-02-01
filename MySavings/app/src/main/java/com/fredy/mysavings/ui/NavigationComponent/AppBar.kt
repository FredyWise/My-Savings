package com.fredy.mysavings.ui.NavigationComponent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fredy.mysavings.Data.Database.Model.UserData
import com.fredy.mysavings.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    onNavigationIconClick: () -> Unit,
//    onSearchButtonClick: () -> Unit,
    onProfilePictureClick: () -> Unit,
    currentUser: UserData,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(text = stringResource(id = R.string.app_name))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = backgroundColor,
            navigationIconContentColor = contentColor,
            titleContentColor = contentColor
        ),
        navigationIcon = {
            IconButton(onClick = onNavigationIconClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Toggle drawer"
                )
            }
        },
        actions = {
//            Box(
//                modifier = Modifier
//                    .padding(
//                        horizontal = 8.dp
//                    )
//                    .size(40.dp)
//                    .clip(CircleShape)
//                    .clickable {
//                        onSearchButtonClick()
//                    },
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Search,
//                    contentDescription = "Search",
//                    modifier = Modifier.size(
//                        30.dp
//                    ),
//                )
//            }
            Box(
                modifier = Modifier
                    .padding(
                        horizontal = 8.dp
                    )
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable {
                        onProfilePictureClick()
                    },
            ) {
                if (currentUser.profilePictureUrl != null && currentUser.profilePictureUrl != "null") {
                    AsyncImage(
                        model = currentUser.profilePictureUrl,
                        contentDescription = "Profile picture",
                        modifier = Modifier.size(
                            40.dp
                        ),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile picture",
                        modifier = Modifier.size(
                            40.dp
                        ),
                    )
                }
            }
        },
    )
}


