package com.fredy.mysavings.ui.Screens.Other

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.fredy.mysavings.ui.Screens.AuthScreen.CustomTextField
import com.fredy.mysavings.ui.Screens.ZCommonComponent.DefaultAppBar

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    onPrimaryColor: Color = MaterialTheme.colorScheme.onPrimary,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onBackground,
    title: String,
) {
    DefaultAppBar(
        modifier = modifier,
        title = title,
        onNavigationIconClick = {},
    ) {
        val context = LocalContext.current
        var username by remember {
            mutableStateOf(
                ""
            )
        }
        var email by remember { mutableStateOf("") }
        var profilePictureUri by remember {
            mutableStateOf<Uri>(
                Uri.EMPTY
            )
        }
        val imagePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->
                uri?.let {
                    profilePictureUri = it
                }
            },
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    18.dp
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                if (profilePictureUri != Uri.EMPTY) {
                    Image(
                        painter = rememberImagePainter(
                            profilePictureUri
                        ),
                        contentDescription = "Profile picture",
                        modifier = Modifier
                            .padding(
                                horizontal = 8.dp
                            )
                            .size(125.dp)
                            .clip(
                                CircleShape
                            ),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile picture",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .padding(
                                horizontal = 8.dp
                            )
                            .size(130.dp)
                            .clip(
                                CircleShape
                            ),
                    )
                }
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = "add image button",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .clip(
                            CircleShape
                        )
                        .clickable {
                            imagePickerLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }
                        .size(40.dp)
                        .background(
                            MaterialTheme.colorScheme.surface
                        )
                        .padding(7.dp),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                label = "Username",
                value = username,
                onValueChange = { username = it },
                placeholder = "typeUsername..."
            )

            CustomTextField(
                label = "Email",
                value = email,
                onValueChange = { email = it },
                placeholder = "typeEmail...",
                keyboardType = KeyboardType.Email
            )
        }
    }
}