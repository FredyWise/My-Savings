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
import com.fredy.mysavings.Data.RoomDatabase.Enum.CategoryType
import com.fredy.mysavings.Data.RoomDatabase.Event.CategoryEvent
import com.fredy.mysavings.Data.categoryIcons
import com.fredy.mysavings.ViewModel.CategoryState
import com.fredy.mysavings.ui.ActionWithName
import com.fredy.mysavings.ui.ChooseIcon
import com.fredy.mysavings.ui.SimpleAddDialog
import com.fredy.mysavings.ui.TypeRadioButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryAddDialog(
    modifier: Modifier = Modifier,
    state: CategoryState,
    onEvent: (CategoryEvent) -> Unit
) {
    SimpleAddDialog(
        modifier = modifier,
        title = if (state.categoryId == null) "Add New Category" else "Update Category",
        onDismissRequest = { onEvent(CategoryEvent.HideDialog) },
        onCancelClicked = { onEvent(CategoryEvent.HideDialog) },
        onSaveClicked = { onEvent(CategoryEvent.SaveCategory) },
    ) {
        TypeRadioButton(
            selectedName = state.categoryType.name,
            radioButtons = listOf(
                ActionWithName(
                    name = CategoryType.Expense.name,
                    action = {
                        onEvent(
                            CategoryEvent.CategoryTypes(
                                CategoryType.Expense
                            )
                        )
                    },
                ), ActionWithName(
                    name = CategoryType.Income.name,
                    action = {
                        onEvent(
                            CategoryEvent.CategoryTypes(
                                CategoryType.Income
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