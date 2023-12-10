package com.fredy.mysavings.ui.Screens.Category

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Data.Database.Entity.Account
import com.fredy.mysavings.Data.Database.Entity.Category
import com.fredy.mysavings.R
import com.fredy.mysavings.ViewModel.CategoryState
import com.fredy.mysavings.ViewModels.Event.AccountEvent
import com.fredy.mysavings.ViewModels.Event.CategoryEvent
import com.fredy.mysavings.ui.Screens.SearchBar
import com.fredy.mysavings.ui.Screens.SimpleButton

@Composable
fun CategoriesScreen(
    modifier: Modifier = Modifier,
    state: CategoryState,
    onEvent: (CategoryEvent) -> Unit,
) {
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
            titleStyle = MaterialTheme.typography.titleLarge.copy(MaterialTheme.colorScheme.onBackground)
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
                onEvent = onEvent
            )
        }
    }
}