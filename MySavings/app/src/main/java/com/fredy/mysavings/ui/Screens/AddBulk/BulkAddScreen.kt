package com.fredy.mysavings.ui.Screens.AddBulk

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberImagePainter
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.fredy.mysavings.R
import com.fredy.mysavings.Util.isTransfer
import com.fredy.mysavings.ViewModels.AccountViewModel
import com.fredy.mysavings.ViewModels.AddSingleRecordViewModel
import com.fredy.mysavings.ViewModels.CategoryViewModel
import com.fredy.mysavings.ViewModels.Event.AddRecordEvent
import com.fredy.mysavings.ui.Screens.Account.AccountAddDialog
import com.fredy.mysavings.ui.Screens.AddSingle.AddBottomSheet
import com.fredy.mysavings.ui.Screens.AddSingle.TextBox
import com.fredy.mysavings.ui.Screens.Category.CategoryAddDialog
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SimpleButton
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch


@OptIn(
    ExperimentalPermissionsApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun BulkAddScreen(
    modifier: Modifier = Modifier,
    onBackground: Color = MaterialTheme.colorScheme.onBackground,
    navigateUp: () -> Unit,
    viewModel: AddSingleRecordViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel(),
    accountViewModel: AccountViewModel = hiltViewModel()

) {
    val state = viewModel.state
    val categoryState by categoryViewModel.state.collectAsStateWithLifecycle()
    val accountState by accountViewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uri = createImageUri(
        context
    )
    var capturedImageUri by remember {
        mutableStateOf<Uri>(
            Uri.EMPTY
        )
    }
    var detectedText: String by remember {
        mutableStateOf(
            ""
        )
    }
    val permissionsState = rememberPermissionState(
        Manifest.permission.CAMERA
    )
    val imageCropLauncher = rememberLauncherForActivityResult(
        CropImageContract()
    ) { result ->
        if (result.isSuccessful) {
            capturedImageUri = result.uriContent!!
            detectTextFromImage(context,
                capturedImageUri,
                { text ->
                    detectedText = text
                },
                { error ->
                    Toast.makeText(
                        context,
                        error,
                        Toast.LENGTH_SHORT
                    ).show()
                })
        } else {
            val exception = result.error
        }
    }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                val cropOption = CropImageContractOptions(it, CropImageOptions())
                imageCropLauncher.launch(cropOption)
            }
        },
    )
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
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

    val sheetState = rememberModalBottomSheetState()
    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }
    var isLeading by remember {
        mutableStateOf(
            true
        )
    }
    if (isSheetOpen) {
        AddBottomSheet(
            sheetState = sheetState,
            onDismissModal = { scope.launch {
                isSheetOpen = it
            } },
            isLeading = isLeading,
            recordType = state.recordType,
            accountState = accountState,
            categoryState = categoryState,
            onEventAccount = accountViewModel::onEvent,
            onEventCategory = categoryViewModel::onEvent,
            onSelectAccount = {
                viewModel.onEvent(
                    AddRecordEvent.AccountIdFromFk(
                        it
                    )
                )
            },
            onSelectCategory = {
                viewModel.onEvent(
                    AddRecordEvent.CategoryIdFk(
                        it
                    )
                )
            }
        )
    }

    if (accountState.isAddingAccount) {
        AccountAddDialog(
            state = accountState,
            onEvent = accountViewModel::onEvent
        )
    }
    if (categoryState.isAddingCategory) {
        CategoryAddDialog(
            state = categoryState,
            onEvent = categoryViewModel::onEvent
        )
    }
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SimpleButton(
                modifier = Modifier,
                onClick = { navigateUp() },
                image = R.drawable.ic_close_foreground,
                imageColor = onBackground,
                title = "CANCEL",
                titleStyle = MaterialTheme.typography.titleMedium.copy(
                    onBackground
                ),
            )
            SimpleButton(
                onClick = {
//                    viewModel.onEvent(
//                        AddRecordEvent.SaveRecord {
//                            accountViewModel.onEvent(
//                                AccountEvent.UpdateAccountBalance(
//                                    state.fromAccount
//                                )
//                            )
//                            navigateUp()
//                        },
//                    )
                },
                image = R.drawable.ic_check_foreground,
                imageColor = onBackground,
                title = "SAVE",
                titleStyle = MaterialTheme.typography.titleMedium.copy(
                    onBackground
                ),
            )
        }
        TextBox(
            value = detectedText,
            onValueChanged = { detectedText = it },
            hintText = "Add Note",
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 4.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                4.dp
            )
        ) {
            Column(
                modifier = Modifier.weight(
                    1f
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isTransfer(
                            state.recordType
                        )
                    ) "From" else "Account",
                    color = onBackground,
                )
                SimpleButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            50.dp
                        )
                        .clip(
                            MaterialTheme.shapes.small
                        )
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.secondary,
                            shape = MaterialTheme.shapes.small
                        ),
                    image = state.fromAccount.accountIcon,
                    imageColor = if (state.fromAccount.accountIconDescription == "") onBackground else Color.Unspecified,
                    onClick = {
                        isLeading = true
                        scope.launch {
                            isSheetOpen = true
                        }
                    },
                    title = state.fromAccount.accountName,
                    titleStyle = MaterialTheme.typography.headlineSmall.copy(
                        onBackground
                    )
                )
            }

            Column(
                modifier = Modifier.weight(
                    1f
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isTransfer(
                            state.recordType
                        )
                    ) "To" else "Category",
                    color = onBackground
                )
                SimpleButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            50.dp
                        )
                        .clip(
                            MaterialTheme.shapes.small
                        )
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.secondary,
                            shape = MaterialTheme.shapes.small
                        ),
                    image = if (isTransfer(
                            state.recordType
                        )
                    ) state.toAccount.accountIcon else state.toCategory.categoryIcon,
                    imageColor = if (state.toCategory.categoryIconDescription != "" && !isTransfer(
                            state.recordType
                        )
                    ) {
                        Color.Unspecified
                    } else if (state.toAccount.accountIconDescription != "" && isTransfer(
                            state.recordType
                        )
                    ) {
                        Color.Unspecified
                    } else {
                        onBackground
                    },
                    onClick = {
                        isLeading = false
                        scope.launch {
                            isSheetOpen = true
                        }
                    },
                    title = if (isTransfer(
                            state.recordType
                        )
                    ) state.toAccount.accountName else state.toCategory.categoryName,
                    titleStyle = MaterialTheme.typography.headlineSmall.copy(
                        onBackground
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (capturedImageUri != Uri.EMPTY) {
            Image(
                contentDescription = "Captured Image",
                painter = rememberImagePainter(
                    capturedImageUri
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(
                        1f
                    )
            )
        } else {
            Icon(
                contentDescription = "Captured Image",
                imageVector = Icons.Default.AddAPhoto,
                tint = onBackground,
                modifier = Modifier
                    .size(100.dp)
                    .weight(
                        1f
                    )
            )
        }

        Row {
            Button(
                onClick = {
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
                modifier = Modifier
                    .weight(1f)
                    .height(
                        48.dp
                    )
            ) {
                Text(text = "Capture Image")
            }
            Button(
                onClick = {
                    imagePickerLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .height(
                        48.dp
                    )
            ) {
                Text(text = "Gallery")
            }
        }
    }
}