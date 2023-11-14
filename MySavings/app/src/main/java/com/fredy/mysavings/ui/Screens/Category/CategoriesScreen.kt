package com.fredy.mysavings.ui.Screens.Category

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fredy.mysavings.Data.RoomDatabase.Entity.Category
import com.fredy.mysavings.Data.RoomDatabase.Event.CategoryEvent
import com.fredy.mysavings.R
import com.fredy.mysavings.ViewModel.CategoryViewModel
import com.fredy.mysavings.ui.SimpleButton

@Composable
fun CategoriesScreen(
    modifier: Modifier = Modifier,
    viewModel: CategoryViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    Column(modifier = modifier) {
        if (state.isAddingCategory) {
            CategoryAddDialog(
                state = state,
                onEvent = viewModel::onEvent
            )
        }
        SimpleButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding( horizontal = 70.dp)
                .clip(
                    MaterialTheme.shapes.medium
                )
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    shape = MaterialTheme.shapes.medium
                ),
            image = R.drawable.ic_add_foreground,
            onClick = {
                viewModel.onEvent(
                    CategoryEvent.ShowDialog(
                        Category(categoryName = "")
                    )
                )
            },
            title = "ADD NEW CATEGORY",
        )
        CategoryBody(
            categoryMaps = state.categories,
            onEvent = viewModel::onEvent
        )
    }
}