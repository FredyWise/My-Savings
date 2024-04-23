package com.fredy.mysavings.Feature.Presentation.Screens.Category

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Util.ActionWithName
import com.fredy.mysavings.Util.DefaultData.categoryIcons
import com.fredy.mysavings.Feature.Presentation.ViewModels.CategoryViewModel.CategoryState
import com.fredy.mysavings.Feature.Presentation.ViewModels.CategoryViewModel.CategoryEvent
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.ChooseIcon
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SimpleDialog
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.TypeRadioButton

@Composable
fun CategoryAddDialog(
    modifier: Modifier = Modifier,
    state: CategoryState,
    onSaveEffect: () -> Unit = {},
    onEvent: (CategoryEvent) -> Unit
) {
    val context = LocalContext.current
    if (state.isAddingCategory) {
        SimpleDialog(
            modifier = modifier,
            dismissOnSave = false,
            title = if (state.categoryId.isEmpty()) "Add New Category" else "Update Category",
            onDismissRequest = { onEvent(CategoryEvent.HideDialog) },
            onSaveClicked = {
                if (state.categoryName.isBlank() || state.categoryIcon == 0 || state.categoryIconDescription.isBlank()) {
                    Toast.makeText(
                        context,
                        "Please Fill All Required Information!!",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    onEvent(CategoryEvent.SaveCategory)
                    onSaveEffect()
                    onEvent(CategoryEvent.HideDialog)
                }
            },
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
                    shape = CircleShape
                ), selectedIcon = state.categoryIcon
            )
        }
    }
}