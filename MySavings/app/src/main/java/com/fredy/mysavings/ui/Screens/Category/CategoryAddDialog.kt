package com.fredy.mysavings.ui.Screens.Category

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Data.Enum.RecordType
import com.fredy.mysavings.Util.ActionWithName
import com.fredy.mysavings.Util.categoryIcons
import com.fredy.mysavings.ViewModel.CategoryState
import com.fredy.mysavings.ViewModels.Event.CategoryEvent
import com.fredy.mysavings.ui.Screens.ZCommonComponent.ChooseIcon
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SimpleDialog
import com.fredy.mysavings.ui.Screens.ZCommonComponent.TypeRadioButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryAddDialog(
    modifier: Modifier = Modifier,
    state: CategoryState,
    onEvent: (CategoryEvent) -> Unit
) {
    SimpleDialog(
        modifier = modifier,
        title = if (state.categoryId.isEmpty()) "Add New Category" else "Update Category",
        onDismissRequest = { onEvent(CategoryEvent.HideDialog) },
        onCancelClicked = { onEvent(CategoryEvent.HideDialog) },
        onSaveClicked = { onEvent(CategoryEvent.SaveCategory) },
    ) {
        TypeRadioButton(
            selectedName = state.categoryType.name,
            radioButtons = listOf(
                ActionWithName(
                    name = RecordType.Expense.name,
                    action = {
                        onEvent(
                            CategoryEvent.CategoryTypes(
                                RecordType.Expense
                            )
                        )
                    },
                ), ActionWithName(
                    name = RecordType.Income.name,
                    action = {
                        onEvent(
                            CategoryEvent.CategoryTypes(
                                RecordType.Income
                            )
                        )
                    },
                )
            )
        )
        TextField(
            value = state.categoryName,
            onValueChange = {
                onEvent(
                    CategoryEvent.CategoryName(
                        it
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 8.dp
                ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    KeyboardActions.Default.onDone
                }),
            label = {
                Text(text = "Name")
            },
            placeholder = {
                Text(text = "Category Name")
            },
        )
        ChooseIcon(
            icons = categoryIcons, onClick = {
                onEvent(
                    CategoryEvent.CategoryIcon(
                        icon = it.image,
                        iconDescription = it.description
                    )
                )
            }, iconModifier = Modifier.clip(
                shape = MaterialTheme.shapes.extraLarge
            ), selectedIcon = state.categoryIcon
        )
    }
}