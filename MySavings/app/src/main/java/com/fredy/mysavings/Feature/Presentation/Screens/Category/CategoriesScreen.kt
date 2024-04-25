package com.fredy.mysavings.Feature.Presentation.Screens.Category

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.navigation.NavHostController
import com.fredy.mysavings.Feature.Domain.Model.Category
import com.fredy.mysavings.R
import com.fredy.mysavings.Feature.Presentation.ViewModels.CategoryViewModel.CategoryState
import com.fredy.mysavings.Feature.Presentation.ViewModels.CategoryViewModel.CategoryEvent
import com.fredy.mysavings.Feature.Presentation.ViewModels.RecordViewModel.RecordEvent
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.ResourceHandler
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SearchBar
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SimpleButton

@Composable
fun CategoriesScreen(
    modifier: Modifier = Modifier,
    rootNavController: NavHostController,
    state: CategoryState,
    onEvent: (CategoryEvent) -> Unit,
    recordEvent: (RecordEvent) -> Unit,
) {
    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }
    CategoryDetailBottomSheet(
        isSheetOpen = isSheetOpen,
        onCloseBottomSheet = { isSheetOpen = it },
        recordEvent = recordEvent,
        state = state
    )

    CategoryAddDialog(
        state = state, onEvent = onEvent, onSaveEffect = {
            recordEvent(RecordEvent.UpdateRecord)
        }
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
        state.categoryResource.let { resource ->
            ResourceHandler(
                resource = resource,
                nullOrEmptyMessage = "You Didn't Have Any Category Yet",
                isNullOrEmpty = { it.isNullOrEmpty() },
                errorMessage = resource.message ?: "",
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
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.secondary,
                                    shape = MaterialTheme.shapes.medium
                                ),
                            image = R.drawable.ic_add_foreground,
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
                    onDeleteCategory = {
                        recordEvent(RecordEvent.UpdateRecord)
                    }
                )
            }
        }
    }
}
