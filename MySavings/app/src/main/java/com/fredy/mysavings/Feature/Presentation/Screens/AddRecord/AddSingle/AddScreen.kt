package com.fredy.mysavings.Feature.Presentation.Screens.AddRecord.AddSingle

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.fredy.mysavings.Feature.Presentation.Screens.AddRecord.AddBottomSheet
import com.fredy.mysavings.Feature.Presentation.Screens.AddRecord.AddConfirmationRow
import com.fredy.mysavings.Feature.Presentation.Screens.AddRecord.AddTextBox
import com.fredy.mysavings.Feature.Presentation.Screens.AddRecord.ChooseAccountAndCategory
import com.fredy.mysavings.Feature.Presentation.Screens.AddRecord.DateAndTimePicker
import com.fredy.mysavings.Feature.Presentation.Screens.AddRecord.LauncherChooserDialog
import com.fredy.mysavings.Feature.Presentation.Screens.Category.CategoryAddDialog
import com.fredy.mysavings.Feature.Presentation.Screens.Wallet.WalletAddDialog
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SimpleAlertDialog
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SimpleWarningDialog
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.TypeRadioButton
import com.fredy.mysavings.Feature.Presentation.Util.ActionWithName
import com.fredy.mysavings.Feature.Presentation.Util.isTransfer
import com.fredy.mysavings.Feature.Presentation.ViewModels.AddRecordViewModel.AddRecordEvent
import com.fredy.mysavings.Feature.Presentation.ViewModels.AddRecordViewModel.AddSingle.AddSingleRecordViewModel
import com.fredy.mysavings.Feature.Presentation.ViewModels.CategoryViewModel.CategoryEvent
import com.fredy.mysavings.Feature.Presentation.ViewModels.CategoryViewModel.CategoryViewModel
import com.fredy.mysavings.Feature.Presentation.ViewModels.WalletViewModel.WalletEvent
import com.fredy.mysavings.Feature.Presentation.ViewModels.WalletViewModel.WalletViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class,
)
@Composable
fun AddScreen(
    modifier: Modifier = Modifier,
    onBackground: Color = MaterialTheme.colorScheme.onBackground,
    navigateUp: () -> Unit,
    viewModel: AddSingleRecordViewModel,
    categoryViewModel: CategoryViewModel,
    walletViewModel: WalletViewModel
) {
    val state = viewModel.state
    val resource by viewModel.resource.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val categoryState by categoryViewModel.state.collectAsStateWithLifecycle()
    val accountState by walletViewModel.state.collectAsStateWithLifecycle()
    val calculatorState = viewModel.calcState
    val scope = rememberCoroutineScope()
    var isLeading by remember {
        mutableStateOf(
            true
        )
    }
    var isShowImage by rememberSaveable {
        mutableStateOf(false)
    }
    var capturedImageUri by remember {
        mutableStateOf<Uri>(
            Uri.EMPTY
        )
    }
    if (isShowImage) {
        SimpleAlertDialog(
            title = "CapturedImage", onDismissRequest = { isShowImage = false },
            rightButton = {
                Button(
                    modifier = Modifier
                        .clip(
                            MaterialTheme.shapes.small
                        ),
                    onClick = { isShowImage = false },
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 6.dp),
                        style = MaterialTheme.typography.titleMedium,
                        text = "Close"
                    )
                }
            },
        ) {
            Column {
                if (capturedImageUri != Uri.EMPTY) {
                    Image(
                        contentDescription = "Captured Image",
                        painter = rememberAsyncImagePainter(
                            capturedImageUri
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                } else {
                    Icon(
                        contentDescription = "Captured Image",
                        imageVector = Icons.Default.ImageNotSupported,
                        tint = onBackground,
                        modifier = Modifier
                            .size(100.dp)
                    )
                }
            }
        }
    }
    var isChoosingLauncher by rememberSaveable {
        mutableStateOf(false)
    }
    LauncherChooserDialog(
        isChoosingLauncher = isChoosingLauncher,
        onDismissRequest = { isChoosingLauncher = false },
        onCapturingImageUri = { capturedImageUri = it },
        detectedText = {
            viewModel.onEvent(
                AddRecordEvent.RecordNotes(
                    it
                )
            )
        }
    )
    LaunchedEffect(
        key1 = resource,
    ) {
        when (resource) {
            is Resource.Error -> {
                if (!state.isShowWarning) {
                    Toast.makeText(
                        context,
                        resource.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            is Resource.Loading -> {

            }

            is Resource.Success -> {
                Toast.makeText(
                    context,
                    resource.data,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    SimpleWarningDialog(
        isShowWarning = state.isShowWarning,
        onDismissRequest = { viewModel.onEvent(AddRecordEvent.DismissWarning) },
        onSaveClicked = {
            viewModel.onEvent(AddRecordEvent.ConvertCurrency)
        },
        warningText = resource.message.toString()
    )

    val sheetState = rememberModalBottomSheetState()
    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
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
            recordType = state.recordType,
            walletState = accountState,
            categoryState = categoryState,
            onEventAccount = walletViewModel::onEvent,
            onEventCategory = categoryViewModel::onEvent,
            onSelectFromAccount = {
                viewModel.onEvent(
                    AddRecordEvent.AccountIdFromFk(
                        it
                    )
                )
            },
            onSelectToAccount = {
                viewModel.onEvent(
                    AddRecordEvent.AccountIdToFk(
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
    WalletAddDialog(
        state = accountState,
        onEvent = walletViewModel::onEvent
    )

    if (categoryState.isAddingCategory) {
        if (state.recordType != categoryState.categoryType) {
            categoryViewModel.onEvent(
                CategoryEvent.CategoryTypes(
                    state.recordType
                )
            )
        }
        CategoryAddDialog(
            state = categoryState,
            onEvent = categoryViewModel::onEvent
        )
    }
    Column(
        modifier = modifier
    ) {
        AddConfirmationRow(
            onCancelClick = { navigateUp() },
            onSaveClick = {
                viewModel.onEvent(
                    AddRecordEvent.SaveRecord {
                        walletViewModel.onEvent(
                            WalletEvent.UpdateWalletBalance(
                                state.fromWallet
                            )
                        )
                        if (isTransfer(
                                state.recordType
                            )
                        ) {
                            walletViewModel.onEvent(
                                WalletEvent.UpdateWalletBalance(
                                    state.toWallet
                                )
                            )
                        }
                        navigateUp()
                    },
                )
            },
        )
        AddTextBox(
            value = state.recordNotes,
            onValueChanged = {
                viewModel.onEvent(
                    AddRecordEvent.RecordNotes(
                        it
                    )
                )
            },
            hintText = "Add Note",
            isImageExist = capturedImageUri != Uri.EMPTY,
            onImageButtonClick = { isShowImage = true },
            onCameraButtonClick = {
                isChoosingLauncher = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(
                    1f
                )
        )
        TypeRadioButton(
            modifier = Modifier.padding(top = 8.dp),
            selectedName = state.recordType.name,
            barHeight = 40.dp,
            radioButtons = listOf(
                ActionWithName(
                    name = RecordType.Expense.name,
                    action = {
                        viewModel.onEvent(
                            AddRecordEvent.RecordTypes(
                                RecordType.Expense
                            )
                        )
                    },
                ), ActionWithName(
                    name = RecordType.Income.name,
                    action = {
                        viewModel.onEvent(
                            AddRecordEvent.RecordTypes(
                                RecordType.Income
                            )
                        )
                    },
                ), ActionWithName(
                    name = RecordType.Transfer.name,
                    action = {
                        viewModel.onEvent(
                            AddRecordEvent.RecordTypes(
                                RecordType.Transfer
                            )
                        )
                    },
                )
            )
        )
        ChooseAccountAndCategory(
            state = state,
            onLeftButtonClick = {
                isLeading = true
                scope.launch {
                    isSheetOpen = true
                }
            },
            onRightButtonClick = {
                isLeading = false
                scope.launch {
                    isSheetOpen = true
                }
            },
        )
        Calculator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp),
            state = calculatorState,
            onAction = viewModel::onAction,
            textStyle = MaterialTheme.typography.displayMedium,
            buttonAspectRatio = 1.8f,
            leadingObject = {
                Text(
                    modifier = Modifier
                        .weight(
                            0.15f
                        )
                        .padding(8.dp),
                    text = state.recordCurrency,
                    color = MaterialTheme.colorScheme.onSecondary,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                )
            },
        )
        DateAndTimePicker(
            applicationContext = context,
            state = state,
            onEvent = viewModel::onEvent,
        )
    }
}
