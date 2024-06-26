package com.fredy.mysavings.ui.Screens.Category

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fredy.mysavings.Data.RoomDatabase.Entity.Category
import com.fredy.mysavings.ViewModels.Event.CategoryEvent
import com.fredy.mysavings.R
import com.fredy.mysavings.ViewModel.CategoryState
import com.fredy.mysavings.ViewModel.CategoryViewModel
import com.fredy.mysavings.ui.Screens.SimpleButton

@Composable
fun CategoriesScreen(
    modifier: Modifier = Modifier,
    state: CategoryState,
    onEvent: (CategoryEvent) -> Unit,
) {
    Column(modifier = modifier) {
        if (state.isAddingCategory) {
            CategoryAddDialog(
                state = state,
                onEvent = onEvent
            )
        }
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
            title = "ADD NEW CATEGORY",
            titleStyle = MaterialTheme.typography.titleLarge.copy(MaterialTheme.colorScheme.onBackground)
        )
        CategoryBody(
            categoryMaps = state.categories,
            onEvent = onEvent
        )
    }
}