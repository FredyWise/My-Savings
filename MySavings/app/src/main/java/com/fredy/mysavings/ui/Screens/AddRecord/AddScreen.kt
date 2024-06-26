package com.fredy.mysavings.ui.Screens.AddRecord

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fredy.mysavings.Data.RoomDatabase.Entity.Account
import com.fredy.mysavings.Data.RoomDatabase.Entity.Category
import com.fredy.mysavings.Data.RoomDatabase.Enum.RecordType
import com.fredy.mysavings.ViewModels.Event.AccountEvent
import com.fredy.mysavings.ViewModels.Event.AddRecordEvent
import com.fredy.mysavings.ViewModels.Event.CategoryEvent
import com.fredy.mysavings.R
import com.fredy.mysavings.Util.isTransfer
import com.fredy.mysavings.ViewModel.AccountViewModel
import com.fredy.mysavings.ViewModel.AddRecordViewModel
import com.fredy.mysavings.ViewModel.CategoryViewModel
import com.fredy.mysavings.ui.Screens.Account.AccountAddDialog
import com.fredy.mysavings.ui.Screens.ActionWithName
import com.fredy.mysavings.ui.Screens.Category.CategoryAddDialog
import com.fredy.mysavings.ui.Screens.SimpleButton
import com.fredy.mysavings.ui.Screens.TypeRadioButton
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterialApi::class
)
@Composable
fun AddScreen(
    modifier: Modifier = Modifier,
    onBackground: Color = MaterialTheme.colorScheme.onBackground,
    id: Int,
    navigateUp: () -> Unit,
    viewModel: AddRecordViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel(),
    accountViewModel: AccountViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val categoryState by categoryViewModel.state.collectAsState()
    val accountState by accountViewModel.state.collectAsState()
    val calculatorState = viewModel.calcState
    val applicationContext = LocalContext.current
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    var isLeft by remember { mutableStateOf(true) }
    viewModel.onEvent(
        AddRecordEvent.SetId(id)
    )
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            if (isLeft) {
                AccountBottomSheet(
                    accounts = accountState.accounts,
                    onSelectAccount = {
                        viewModel.onEvent(
                            AddRecordEvent.AccountIdFromFk(
                                it
                            )
                        )
                        scope.launch {
                            scaffoldState.bottomSheetState.collapse()
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
                            scaffoldState.bottomSheetState.collapse()
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
                            scaffoldState.bottomSheetState.collapse()
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
        },
    ) {
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
                            isLeft = true
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
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
                            isLeft = false
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
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
                state = calculatorState,
                onAction = viewModel::onAction,
                textStyle = MaterialTheme.typography.displayMedium,
                buttonAspectRatio = 1.8f,
                modifier = Modifier.fillMaxWidth()
            )
            DateAndTimePicker(
                applicationContext = applicationContext,
                state = state,
                onEvent = viewModel::onEvent,
            )
        }
    }
}
