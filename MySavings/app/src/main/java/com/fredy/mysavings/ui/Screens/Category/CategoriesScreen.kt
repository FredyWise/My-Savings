package com.fredy.mysavings.ui.Screens.Category

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Data.Database.Entity.Category
import com.fredy.mysavings.R
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.ViewModels.CategoryState
import com.fredy.mysavings.ViewModels.Event.CategoryEvent
import com.fredy.mysavings.ui.Screens.ZCommonComponent.LoadingAnimation
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SearchBar
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SimpleButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    modifier: Modifier = Modifier,
    state: CategoryState,
    onEvent: (CategoryEvent) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }
    if (isSheetOpen) {
        ModalBottomSheet(
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            onDismissRequest = {
                isSheetOpen = false
            },
            dragHandle = {},
        ) {
            state.recordMapsResource.let { resource ->
                if (resource is Resource.Success && !resource.data.isNullOrEmpty()) {
                    CategoryDetailSheet(
                        recordMaps = resource.data,
                        icon = state.category.categoryIcon,
                        iconDescription = state.category.categoryIconDescription,
                        itemName = state.category.categoryName,
                        itemInfo = "Category Type: " + state.category.categoryType.name,
                        onBackIconClick = {
                            isSheetOpen = false
                        },
                    )
                } else {
                    LoadingAnimation(
                        isLoading = resource is Resource.Loading,
                        notLoadingMessage = "You haven't made any Record using this category yet",
                        onClick = {
                            isSheetOpen = false
                        },
                    )
                }
            }
        }
    }

    if (state.isAddingCategory) {
        CategoryAddDialog(
            state = state, onEvent = onEvent
        )
    }
    Column(modifier = modifier) {
        SimpleButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 50.dp
                )
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
        SearchBar(
            searchText = state.searchText,
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
        ) {
            CategoryBody(
                categoryMaps = state.categories,
                onEvent = onEvent,
                onEntityClick = {
                    isSheetOpen = true
                },
            )
        }
    }
}
