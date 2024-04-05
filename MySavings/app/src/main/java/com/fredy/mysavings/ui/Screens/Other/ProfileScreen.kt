package com.fredy.mysavings.ui.Screens.Other

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.fredy.mysavings.Feature.Data.Database.Model.UserData
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.TAG
import com.fredy.mysavings.Util.isValidEmailOrPhone
import com.fredy.mysavings.Util.isValidPassword
import com.fredy.mysavings.ViewModels.AuthState
import com.fredy.mysavings.ViewModels.Event.AuthEvent
import com.fredy.mysavings.ui.Screens.AuthScreen.CustomTextField
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
    ) {
        val context = LocalContext.current
        var profilePictureUri by remember {
            mutableStateOf<Uri>(
                Uri.EMPTY
            )
        }
        var username by remember { mutableStateOf(currentUserData.username ?: "") }
//        var emailOrPhone by remember { mutableStateOf(currentUserData.emailOrPhone ?: "") }
        var oldPassword by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }
        var confirmPassword by remember {
            mutableStateOf("")
        }
        var isChangeCredentials by remember {
            mutableStateOf(false)
        }
        val imagePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->
                uri?.let {
                    profilePictureUri = it
                }
            },
        )
        LaunchedEffect(key1 = state.updateResource){
            Log.e(TAG, "ProfileScreen: ${state.updateResource}", )
            when(state.updateResource){
                is Resource.Success -> {
                    val message = state.updateResource.data.orEmpty()
                    if (message.isNotEmpty()) {
                        Toast.makeText(
                            context,
                            message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                is Resource.Error -> {
                    val message = state.updateResource.message.orEmpty()
                    if (message.isNotEmpty()) {
                        Toast.makeText(
                            context,
                            message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                is Resource.Loading -> {}
            }
        }

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
            AnimatedVisibility(isChangeCredentials) {
                Column {
//                    Spacer(modifier = Modifier.height(8.dp))
//                    CustomTextField(
//                        label = "Email",
//                        value = emailOrPhone,
//                        onValueChange = { emailOrPhone = it },
//                        placeholder = "type Email...",
//                        keyboardType = KeyboardType.Email
//                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CustomTextField(
                        label = "Old Password",
                        value = oldPassword,
                        onValueChange = { oldPassword = it },
                        placeholder = "typePassword...",
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardType = KeyboardType.Password
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CustomTextField(
                        label = "New Password",
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        placeholder = "typePassword...",
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardType = KeyboardType.Password
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CustomTextField(
                        label = "Confirm Password",
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = "typeConfirmPassword...",
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardType = KeyboardType.Password
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Change Credentials? ${if (isChangeCredentials) "Yes" else "No"}",
                modifier = Modifier.clickable {
                    isChangeCredentials = isChangeCredentials.not()
                },
                fontWeight = FontWeight.Bold,
                color = onBackgroundColor,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    onEvent(
                        AuthEvent.UpdateUserData(
//                            email = emailOrPhone,
                            username = username,
                            oldPassword = oldPassword,
                            password = newPassword,
                            photoUrl = profilePictureUri
                        )
                    )
                },
                enabled = if (isChangeCredentials) {
                    /*isValidEmailOrPhone(emailOrPhone) &&*/ (isValidPassword(newPassword) || newPassword.isEmpty()) && (newPassword == confirmPassword) && oldPassword.isNotEmpty()
                } else true,
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
                    else ->{
                        Text(
                            text = "Save User Data",
                            style = MaterialTheme.typography.titleMedium,
                            color = onPrimaryColor,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    }
}
