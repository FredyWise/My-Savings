package com.fredy.category.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.fredy.category.viewModel.CategoryEvent
import com.fredy.category.viewModel.CategoryState
import com.fredy.domain.util.SavingsIcons.AddCircleOutlineIcon
import com.fredy.domain.model.Category
import com.fredy.domain.model.TrueRecord
import com.fredy.ui.components.button.SimpleButton
import com.fredy.ui.components.handler.ResourceHandler
import com.fredy.ui.components.list.SearchBar

@Composable
fun CategoriesScreen(
    modifier: Modifier = Modifier,
    state: CategoryState,
    onEvent: (CategoryEvent) -> Unit,
    onUpdateRecord: () -> Unit,
    onShowRecordDialog: (TrueRecord) -> Unit
) {
    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }
    CategoryDetailBottomSheet(
        isSheetOpen = isSheetOpen,
        onCloseBottomSheet = { isSheetOpen = it },
        onShowRecordDialog = onShowRecordDialog,
        state = state
    )

    CategoryAddDialog(
        state = state, onEvent = onEvent, onSaveEffect = onUpdateRecord
    )

    Column(modifier = modifier) {
        SearchBar(
            searchText = state.searchQuery,
            onValueChange = {
                onEvent(
                    CategoryEvent.SearchCategory(
                        it
                    )
                )
            },
            isSearching = state.isSearching,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
        )
        state.categoryMapsResource.let { resource ->
            ResourceHandler(
                resource = resource,
                nullOrEmptyMessage = "You Didn't Have Any Category Yet",
                isNullOrEmpty = { it.isNullOrEmpty() },
                onMessageClick = {
                    onEvent(
                        CategoryEvent.ShowDialog(
                            Category(
                                categoryName = ""
                            )
                        )
                    )
                },
            ) { data ->
                CategoryBody(
                    categoryMaps = data,
                    topItem = {
                        SimpleButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 50.dp
                                )
                                .padding(top = 16.dp)
                                .clip(
                                    MaterialTheme.shapes.medium
                                )
                                .border(
                                    width = 3.dp / 2,
                                    color = MaterialTheme.colorScheme.secondary,
                                    shape = MaterialTheme.shapes.medium
                                ),
                            image = AddCircleOutlineIcon.image,
                            imageColor = MaterialTheme.colorScheme.onBackground,
                            onClick = {
                                onEvent(
                                    CategoryEvent.ShowDialog(
                                        Category(categoryName = "")
                                    )
                                )
                            },
                            title = "Add New Category ",
                            titleStyle = MaterialTheme.typography.titleLarge.copy(
                                MaterialTheme.colorScheme.onBackground
                            )
                        )
                    },
                    onEvent = onEvent,
                    onEntityClick = {
                        isSheetOpen = true
                    },
                    onDeleteCategory = onUpdateRecord
                )
            }
        }
    }
}
