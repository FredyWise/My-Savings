package com.fredy.mysavings.ui.Screens.Category

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fredy.mysavings.Data.Database.Model.Category
import com.fredy.mysavings.R
import com.fredy.mysavings.Util.formatBalanceAmount
import com.fredy.mysavings.Util.formatTime
import com.fredy.mysavings.Util.isTransfer
import com.fredy.mysavings.ViewModels.CategoryState
import com.fredy.mysavings.ViewModels.Event.CategoryEvent
import com.fredy.mysavings.ViewModels.Event.RecordsEvent
import com.fredy.mysavings.ViewModels.RecordState
import com.fredy.mysavings.ui.NavigationComponent.Navigation.NavigationRoute
import com.fredy.mysavings.ui.Screens.Record.RecordDialog
import com.fredy.mysavings.ui.Screens.ZCommonComponent.DetailAppBar
import com.fredy.mysavings.ui.Screens.ZCommonComponent.ResourceHandler
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SearchBar
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SimpleButton
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SimpleEntityItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    modifier: Modifier = Modifier,
    rootNavController: NavHostController,
    state: CategoryState,
    onEvent: (CategoryEvent) -> Unit,
    recordState: RecordState,
    recordEvent: (RecordsEvent) -> Unit,
) {
    recordState.trueRecord?.let {
        RecordDialog(
            trueRecord = it,
            onSaveClicked = { record ->
                recordEvent(
                    RecordsEvent.DeleteRecord(
                        record
                    )
                )
            },
            onDismissDialog = {
                recordEvent(
                    RecordsEvent.HideDialog
                )
            },
            onEdit = {
                rootNavController.navigate(
                    NavigationRoute.Add.route + "/" + it.record.recordId
                )
            },
        )
    }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }
    if (isSheetOpen) {
        ModalBottomSheet(
            modifier = Modifier.padding(top = 24.dp),
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            onDismissRequest = {
                isSheetOpen = false
            },
            dragHandle = {},
        ) {
            DetailAppBar(
                title = "Category details",
                resource = state.recordMapsResource,
                icon = state.category.categoryIcon,
                iconDescription = state.category.categoryIconDescription,
                itemName = state.category.categoryName,
                itemInfo = "Category Type: " + state.category.categoryType.name,
                onNavigationIconClick = {
                    isSheetOpen = false
                },
            ) { item, onBackgroundColor, balanceColor ->
                SimpleEntityItem(
                    modifier = Modifier
                        .padding(
                            vertical = 4.dp
                        )
                        .clickable {
                            recordEvent(
                                RecordsEvent.ShowDialog(item)
                            )
                        },
                    iconModifier = Modifier
                        .size(
                            40.dp
                        )
                        .clip(
                            shape = CircleShape
                        ),
                    icon = item.fromAccount.accountIcon,
                    iconDescription = item.fromAccount.accountIconDescription,
                    endContent = {
                        Text(
                            text = formatTime(
                                item.record.recordDateTime.toLocalTime()
                            ),
                            color = onBackgroundColor,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                ) {
                    Text(
                        text = if (isTransfer(item.record.recordType)) {
                            item.fromAccount.accountName + " -> " + item.toAccount.accountName
                        } else item.fromAccount.accountName,
                        color = onBackgroundColor,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1
                    )
                    Text(
                        text = formatBalanceAmount(
                            item.record.recordAmount,
                            item.record.recordCurrency,
                            true
                        ),
                        color = balanceColor,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1
                    )
                }
            }
        }
    }

    if (state.isAddingCategory) {
        CategoryAddDialog(
            state = state, onEvent = onEvent, onSaveEffect = {
                recordEvent(RecordsEvent.UpdateRecord)
            }
        )
    }
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
                        recordEvent(RecordsEvent.UpdateRecord)
                    }
                )
            }
        }
    }
}
