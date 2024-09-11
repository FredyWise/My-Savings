package com.fredy.mysavings.Feature.Presentation.Screens.AddRecord

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fredy.domain.enums.RecordType
import com.fredy.mysavings.Feature.Presentation.Screens.AddRecord.AddBottomSheet
import com.fredy.mysavings.Feature.Presentation.Screens.AddRecord.AddBulk.ChooseAccountAndMultiCategory
import com.fredy.mysavings.Feature.Presentation.Screens.AddRecord.AddBulk.RecordAddDialog
import com.fredy.mysavings.Feature.Presentation.Screens.AddRecord.AddBulk.RecordList
import com.fredy.mysavings.Feature.Presentation.Screens.AddRecord.AddConfirmationRow
import com.fredy.mysavings.Feature.Presentation.Screens.AddRecord.AddTextBox
import com.fredy.mysavings.Feature.Presentation.Screens.AddRecord.DateAndTimePicker
import com.fredy.mysavings.Feature.Presentation.Screens.AddRecord.ImageDialog
import com.fredy.mysavings.Feature.Presentation.Screens.AddRecord.LauncherChooserDialog
import com.fredy.mysavings.Feature.Presentation.Screens.Category.CategoryAddDialog
import com.fredy.mysavings.Feature.Presentation.Screens.Wallet.WalletAddDialog
import com.fredy.mysavings.Feature.Presentation.ViewModels.AddRecordViewModel.AddBulk.AddBulkRecordViewModel
import com.fredy.mysavings.Feature.Presentation.ViewModels.AddRecordViewModel.AddRecordEvent
import com.fredy.mysavings.Feature.Presentation.ViewModels.CategoryViewModel.CategoryViewModel
import com.fredy.mysavings.Feature.Presentation.ViewModels.WalletViewModel.WalletEvent
import com.fredy.mysavings.Feature.Presentation.ViewModels.WalletViewModel.WalletViewModel
import kotlinx.coroutines.launch


@OptIn(
    ExperimentalMaterial3Api::class
)
@Composable
fun BulkAddScreen(
    modifier: Modifier = Modifier,
    onBackground: Color = MaterialTheme.colorScheme.onBackground,
    navigateUp: () -> Unit,
    viewModel: AddBulkRecordViewModel,
    categoryViewModel: CategoryViewModel,
    walletViewModel: WalletViewModel,
    onConvertImageToRecord: (Uri) -> Unit,
    onRecordNotesChange: (String) -> Unit,


    ) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isShowImage by rememberSaveable {
        mutableStateOf(false)
    }
    var capturedImageUri by remember {
        mutableStateOf<Uri>(
            Uri.EMPTY
        )
    }
    var recordType by remember {
        mutableStateOf(RecordType.Expense)
    }
    ImageDialog(
        isShowImage = isShowImage,
        capturedImageUri = capturedImageUri,
        onDismissRequest = { isShowImage = false },
    )
    var isChoosingLauncher by rememberSaveable {
        mutableStateOf(false)
    }
    LauncherChooserDialog(
        isChoosingLauncher = isChoosingLauncher,
        onDismissRequest = { isChoosingLauncher = false },
        onCapturingImageUri = {
            capturedImageUri = it
            onConvertImageToRecord(it)
        },
        detectedText = {
            onRecordNotesChange(it)
        }
    )
    LaunchedEffect(
        key1 = resource,
    ) {
        when (resource) {
            is Resource.Error -> {
                Toast.makeText(
                    context,
                    resource.message,
                    Toast.LENGTH_LONG
                ).show()
            }

            is Resource.Loading -> {

            }

            is Resource.Success -> {
                Toast.makeText(
                    context,
                    "Record Successfully Added",
                    Toast.LENGTH_LONG
                ).show()
            }
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
            onDismissModal = {
                scope.launch {
                    isSheetOpen = it
                }
            },
            isLeading = isLeading,
            recordType = recordType,
            walletState = walletState,
            categoryState = categoryState,
            onEventAccount = walletEvent,
            onEventCategory = categoryEvent,
            onSelectFromAccount = {
                onEvent(
                    AddRecordEvent.AccountIdFromFk(
                        it
                    )
                )
            },
            onSelectToAccount = {
                onEvent(
                    AddRecordEvent.AccountIdToFk(
                        it
                    )
                )
            },
            onSelectCategory = {
                onEvent(
                    AddRecordEvent.CategoryIdFk(
                        it
                    )
                )
            }
        )
    }
    WalletAddDialog(
        state = walletState,
        onEvent = walletEvent
    )
    CategoryAddDialog(
        state = categoryState,
        onEvent = categoryEvent
    )

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AddConfirmationRow(
            onCancelClick = { navigateUp() },
            onSaveClick = {
                onEvent(
                    AddRecordEvent.SaveRecord {
                        walletViewModel.onEvent(
                            WalletEvent.UpdateWalletBalance(
                                state.fromWallet
                            )
                        )
                        navigateUp()
                    },
                )
            },
        )
        AddTextBox(
            value = state.recordNotes,
            onValueChanged = {
                onEvent(
                    AddRecordEvent.RecordNotes(
                        it
                    )
                )
            },
            hintText = "Text extraction result will show here",
            isImageExist = capturedImageUri != Uri.EMPTY,
            onImageButtonClick = { isShowImage = true },
            onCameraButtonClick = {
                isChoosingLauncher = true
            },
            modifier = Modifier.heightIn(100.dp, 150.dp)
        )
        ChooseAccountAndMultiCategory(
            state = state,
            onTopButtonClick = {
                isLeading = true
                scope.launch {
                    isSheetOpen = true
                }
            },
            onLeftButtonClick = {
                isLeading = false
                recordType = RecordType.Expense
                scope.launch {
                    isSheetOpen = true
                }
            },
            onRightButtonClick = {
                isLeading = false
                recordType = RecordType.Income
                scope.launch {
                    isSheetOpen = true
                }
            },
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .weight(
                    1f
                ),
            verticalArrangement = Arrangement.Center
        ) {
            if (capturedImageUri != Uri.EMPTY) {
                RecordList(
                    Modifier
                        .weight(
                            1f
                        ),
                    records = state.records,
                    onItemClick = { onEvent(AddRecordEvent.ShowAddRecordItemDialog(it)) }
                )
            } else {
                Icon(
                    contentDescription = "Captured Image",
                    imageVector = Icons.Default.AddAPhoto,
                    tint = onBackground,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            isChoosingLauncher = true
                        }
                        .padding(20.dp)
                        .size(100.dp)
                )
            }
        }
        DateAndTimePicker(
            applicationContext = context,
            state = state,
            onEvent = viewModel::onEvent,
        )
    }
}