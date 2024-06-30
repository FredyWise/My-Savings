package com.fredy.mysavings.Feature.Presentation.Screens.AddRecord

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SimpleAlertDialog
import com.fredy.mysavings.Feature.Presentation.ViewModels.AddRecordViewModel.createImageUri
import com.fredy.mysavings.Feature.Presentation.ViewModels.AddRecordViewModel.detectTextFromImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LauncherChooserDialog(
    modifier: Modifier = Modifier,
    isChoosingLauncher: Boolean,
    onCapturingImageUri: (Uri) -> Unit,
    detectedText: (String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current
    val uri = createImageUri(
        context
    )
    val permissionsState = rememberPermissionState(
        Manifest.permission.CAMERA
    )
    val imageCropLauncher = rememberLauncherForActivityResult(
        CropImageContract()
    ) { result ->
        if (result.isSuccessful) {
            val capturedImageUri = result.uriContent!!
            onCapturingImageUri(capturedImageUri)
            detectTextFromImage(context,
                capturedImageUri,
                { text ->
                    detectedText(text)
                },
                { error ->
                    Toast.makeText(
                        context,
                        error,
                        Toast.LENGTH_SHORT
                    ).show()
                })
        } else {
            Toast.makeText(
                context,
                result.error?.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                onDismissRequest()
                val cropOption = CropImageContractOptions(it, CropImageOptions())
                imageCropLauncher.launch(cropOption)
            }
        },
    )
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            onDismissRequest()
            val cropOption = CropImageContractOptions(uri, CropImageOptions())
            imageCropLauncher.launch(cropOption)

        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(
                context,
                "Permission Granted",
                Toast.LENGTH_SHORT
            ).show()
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(
                context,
                "Permission Denied",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    if (isChoosingLauncher) {
        SimpleAlertDialog(
            modifier = modifier, title = "Pick Image From: ", onDismissRequest = onDismissRequest,
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                Icon(
                    modifier = Modifier
                        .size(75.dp)
                        .clickable {
                            if (permissionsState.status.isGranted) {
                                cameraLauncher.launch(
                                    uri
                                )
                            } else {
                                permissionLauncher.launch(
                                    Manifest.permission.CAMERA
                                )
                            }
                        },
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = "Camera"
                )
                Icon(
                    modifier = Modifier
                        .size(75.dp)
                        .clickable {
                            imagePickerLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                    imageVector = Icons.Default.Image,
                    contentDescription = "Image"
                )
            }
        }
    }
}