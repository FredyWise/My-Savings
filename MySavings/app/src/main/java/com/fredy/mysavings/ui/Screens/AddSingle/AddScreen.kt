package com.fredy.mysavings.ui.Screens.AddSingle

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Data.Database.Entity.Account
import com.fredy.mysavings.Data.Database.Entity.Category
import com.fredy.mysavings.Data.Enum.RecordType
import com.fredy.mysavings.R
import com.fredy.mysavings.Util.ActionWithName
import com.fredy.mysavings.Util.isTransfer
import com.fredy.mysavings.ViewModels.AccountViewModel
import com.fredy.mysavings.ViewModels.AddSingleRecordViewModel
import com.fredy.mysavings.ViewModels.CategoryViewModel
import com.fredy.mysavings.ViewModels.Event.AccountEvent
import com.fredy.mysavings.ViewModels.Event.AddRecordEvent
import com.fredy.mysavings.ViewModels.Event.CategoryEvent
import com.fredy.mysavings.ui.Screens.Account.AccountAddDialog
import com.fredy.mysavings.ui.Screens.Category.CategoryAddDialog
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SimpleButton
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SimpleDialog
import com.fredy.mysavings.ui.Screens.ZCommonComponent.TypeRadioButton
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class,
)
@Composable
fun AddScreen(
    modifier: Modifier = Modifier,
    onBackground: Color = MaterialTheme.colorScheme.onBackground,
    id: String,
    navigateUp: () -> Unit,
    viewModel: AddSingleRecordViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel(),
    accountViewModel: AccountViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val resource = viewModel.resource.value
    val context = LocalContext.current
    val categoryState by categoryViewModel.state.collectAsStateWithLifecycle()
    val accountState by accountViewModel.state.collectAsStateWithLifecycle()
    val calculatorState = viewModel.calcState
    val scope = rememberCoroutineScope()
    var isLeading by remember {
        mutableStateOf(
            true
        )
    }
    var isShowWarning by remember {
        mutableStateOf(
            false
        )
    }
    viewModel.onEvent(
        AddRecordEvent.SetId(id)
    )
    LaunchedEffect(
        key1 = resource.error,
    ) {
        if (!resource.error.isNullOrEmpty()) {
            val error = resource.error
            Toast.makeText(
                context,
                "${error}",
                Toast.LENGTH_LONG
            ).show()
        }
        if (!resource.success.isNullOrEmpty()) {
            isShowWarning = true
        }
    }
    if (isShowWarning){
        SimpleDialog(
            title = resource.success!!,
            cancelName = "No",
            saveName = "Yes",
            onDismissRequest = { isShowWarning = false },
            onCancelClicked = { isShowWarning = false },
            onSaveClicked = { /*TODO*/ }) {
        }
    }

    val sheetState = rememberModalBottomSheetState()
    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }
    if (isSheetOpen) {
        ModalBottomSheet(
            sheetState = sheetState,
            dragHandle = {},
            onDismissRequest = {
                isSheetOpen = false
            },
        ) {
            if (isLeading) {
                AccountBottomSheet(
                    accounts = accountState.accounts,
                    onSelectAccount = {
                        viewModel.onEvent(
                            AddRecordEvent.AccountIdFromFk(
                                it
                            )
                        )
                        scope.launch {
                            isSheetOpen = false
                        }
                    },
                    onAddAccount = {
                        accountViewModel.onEvent(
                            AccountEvent.ShowDialog(
                                Account(
                                    accountName = ""
                                )
                            )
                        )
                    },
                )
            } else if (isTransfer(state.recordType)) {
                AccountBottomSheet(
                    accounts = accountState.accounts,
                    onSelectAccount = {
                        viewModel.onEvent(
                            AddRecordEvent.AccountIdToFk(
                                it
                            )
                        )
                        scope.launch {
                            isSheetOpen = false
                        }
                    },
                    onAddAccount = {
                        accountViewModel.onEvent(
                            AccountEvent.ShowDialog(
                                Account(
                                    accountName = ""
                                )
                            )
                        )
                    },
                )
            } else {
                CategoryBottomSheet(
                    categoryMaps = categoryState.categories,
                    recordType = state.recordType,
                    onSelectCategory = {
                        viewModel.onEvent(
                            AddRecordEvent.CategoryIdFk(
                                it
                            )
                        )
                        scope.launch {
                            isSheetOpen = false
                        }
                    },
                    onAddCategory = {
                        categoryViewModel.onEvent(
                            CategoryEvent.ShowDialog(
                                Category(
                                    categoryName = ""
                                )
                            )
                        )
                    },
                )
            }
        }
    }

    if (accountState.isAddingAccount) {
        AccountAddDialog(
            state = accountState,
            onEvent = accountViewModel::onEvent
        )
    }
    if (categoryState.isAddingCategory) {
        if (state.recordType != categoryState.categoryType){
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
                    viewModel.onEvent(
                        AddRecordEvent.SaveRecord {
                            accountViewModel.onEvent(
                                AccountEvent.UpdateAccountBalance(
                                    state.fromAccount
                                )
                            )
                            if (isTransfer(
                                    state.recordType
                                )) {
                                accountViewModel.onEvent(
                                    AccountEvent.UpdateAccountBalance(
                                        state.toAccount
                                    )
                                )
                            }
                            Toast.makeText(
                                context,
                                "Add Success",
                                Toast.LENGTH_SHORT
                            ).show()
                            navigateUp()
                        },
                    )
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
            value = state.recordNotes,
            onValueChanged = {
                viewModel.onEvent(
                    AddRecordEvent.RecordNotes(
                        it
                    )
                )
            },
            hintText = "Add Note",
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
                        )) "From" else "Account",
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
                        )) "To" else "Category",
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
                        )) state.toAccount.accountIcon else state.toCategory.categoryIcon,
                    imageColor = if (state.toCategory.categoryIconDescription != "" && !isTransfer(
                            state.recordType
                        )) {
                        Color.Unspecified
                    } else if (state.toAccount.accountIconDescription != "" && isTransfer(
                            state.recordType
                        )) {
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
                        )) state.toAccount.accountName else state.toCategory.categoryName,
                    titleStyle = MaterialTheme.typography.headlineSmall.copy(
                        onBackground
                    )
                )
            }
        }
        Calculator(
            modifier = Modifier.fillMaxWidth(),
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
