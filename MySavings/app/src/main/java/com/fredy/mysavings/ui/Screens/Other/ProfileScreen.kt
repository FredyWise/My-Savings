package com.fredy.mysavings.ui.Screens.Other

import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.fredy.mysavings.Data.Database.Model.UserData
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.currencyCodes
import com.fredy.mysavings.Util.isValidEmailOrPhone
import com.fredy.mysavings.ViewModels.AuthState
import com.fredy.mysavings.ViewModels.Event.AuthEvent
import com.fredy.mysavings.ui.Screens.AuthScreen.CustomTextField
import com.fredy.mysavings.ui.Screens.ZCommonComponent.CurrencyDropdown
import com.fredy.mysavings.ui.Screens.ZCommonComponent.DefaultAppBar

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    onPrimaryColor: Color = MaterialTheme.colorScheme.onPrimary,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onBackground,
    rootNavController: NavController,
    title: String,
    currentUserData: UserData,
    state: AuthState,
    onEvent: (AuthEvent) -> Unit
) {
    DefaultAppBar(
        modifier = modifier,
        title = title,
        onNavigationIconClick = { rootNavController.navigateUp() },
        scrollable = false,
    ) {
        val context = LocalContext.current
        var username by remember {
            mutableStateOf(
                currentUserData.username ?: ""
            )
        }
        var preferredCurrency by remember {
            mutableStateOf(
                currentUserData.userCurrency
            )
        }
        var emailOrPhone by remember { mutableStateOf(currentUserData.emailOrPhone ?: "") }
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
                        painter = rememberAsyncImagePainter(
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
                } else if (currentUserData.profilePictureUrl != null && currentUserData.profilePictureUrl != "null") {
                    AsyncImage(
                        model = currentUserData.profilePictureUrl,
                        contentDescription = "Profile picture",
                        modifier = Modifier
                            .size(
                                125.dp
                            )
                            .clip(CircleShape),
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
                placeholder = "type Username..."
            )
            Spacer(modifier = Modifier.height(8.dp))

            CustomTextField(
                label = "Email",
                value = emailOrPhone,
                onValueChange = { emailOrPhone = it },
                placeholder = "type Email...",
                keyboardType = KeyboardType.Email
            )
            Spacer(modifier = Modifier.height(16.dp))

            CurrencyDropdown(selectedText = preferredCurrency, onClick = { preferredCurrency = it })
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    onEvent(
                        AuthEvent.UpdateUserData(
                            emailOrPhone,
                            username,
                            preferredCurrency,
                            profilePictureUri
                        )
                    )
                },
                enabled = isValidEmailOrPhone(
                    emailOrPhone
                ) && currencyCodes.contains(preferredCurrency),
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = primaryColor.copy(
                        0.7f
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 30.dp
                    ),
                shape = RoundedCornerShape(15.dp)
            ) {
                when (state.updateResource) {
                    is Resource.Loading -> {
                        CircularProgressIndicator(
                            color = onPrimaryColor
                        )
                    }

                    is Resource.Success -> {
                        LaunchedEffect(
                            key1 = state.updateResource,
                        ) {
                            Toast.makeText(
                                context,
                                state.updateResource.data,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        null
                    }

                    is Resource.Error -> {
                        null
                    }
                } ?: Text(
                    text = "Save User Data",
                    style = MaterialTheme.typography.titleMedium,
                    color = onPrimaryColor,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}