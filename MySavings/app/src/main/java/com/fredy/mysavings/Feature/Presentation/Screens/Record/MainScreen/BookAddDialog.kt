package com.fredy.mysavings.Feature.Presentation.Screens.Record.MainScreen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Util.DefaultData
import com.fredy.mysavings.Feature.Presentation.ViewModels.BookViewModel.BookState
import com.fredy.mysavings.Feature.Presentation.ViewModels.BookViewModel.BookEvent
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.ChooseIcon
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SimpleDialog
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SimpleWarningDialog

@Composable
fun BookAddDialog(
    modifier: Modifier = Modifier,
    state: BookState,
    onSaveEffect: () -> Unit = {},
    onEvent: (BookEvent) -> Unit
) {
    val context = LocalContext.current
    if (state.isAddingBook) {
        val isAdding = state.bookId.isEmpty()
        var isShowWarning by remember { mutableStateOf(false) }
        SimpleWarningDialog(
            isShowWarning = isShowWarning,
            onDismissRequest = { isShowWarning = false },
            onSaveClicked = {
                onEvent(
                    BookEvent.DeleteBook(
                        book = state.book,
                        onDeleteEffect = { onEvent(BookEvent.HideDialog) },
                    )
                )
            },
            warningText = "Are You Sure Want to Delete This Book? \nall record inside the book will also be deleted!!"
        )
        SimpleDialog(
            modifier = modifier,
            dismissOnSave = false,
            title = if (isAdding) "Add New Book" else "Update Book",
            additionalExitButton = {
                if (!isAdding) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(35.dp)
                                .clip(CircleShape)
                                .clickable { isShowWarning = true }
                                .padding(5.dp),
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete button",
                        )
                    }
                }
            },
            onDismissRequest = { onEvent(BookEvent.HideDialog) },
            onSaveClicked = {
                if (state.bookName.isBlank() || state.bookIcon == 0 || state.bookIconDescription.isBlank()) {
                    Toast.makeText(
                        context,
                        "Please Fill All Required Information!!",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    onEvent(BookEvent.SaveBook)
                    onSaveEffect()
                    onEvent(BookEvent.HideDialog)
                }
            },
        ) {
            TextField(
                value = state.bookName,
                onValueChange = {
                    onEvent(
                        BookEvent.BookName(
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
                    Text(text = "Book Name")
                },
            )
            ChooseIcon(
                icons = DefaultData.allSavingsIcons, onClick = {
                    onEvent(
                        BookEvent.BookIcon(
                            icon = it.image,
                            iconDescription = it.description
                        )
                    )
                }, iconModifier = Modifier.clip(
                    shape = CircleShape
                ), selectedIcon = state.bookIcon
            )
        }
    }
}