package com.fredy.mysavings.ui.Screens.Category

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Data.RoomDatabase.Event.CategoryEvent
import com.fredy.mysavings.Data.isTransfer
import com.fredy.mysavings.ViewModel.CategoryMap
import com.fredy.mysavings.ui.Screens.ActionWithName
import com.fredy.mysavings.ui.Screens.AdvancedEntityItem
import com.fredy.mysavings.ui.Screens.CustomStickyHeader

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryBody(
    modifier: Modifier = Modifier,
    categoryMaps: List<CategoryMap>,
    onEvent: (CategoryEvent) -> Unit,
) {
    LazyColumn(modifier = modifier) {
        categoryMaps.forEach { categoryMap ->
            if (!isTransfer(categoryMap.categoryType)) {
                stickyHeader {
                    CustomStickyHeader(
                        modifier = Modifier.background(
                            MaterialTheme.colorScheme.background
                        ),
                        title = categoryMap.categoryType.name + " Categories",
                        textStyle = MaterialTheme.typography.titleLarge
                    )
                }
                items(categoryMap.categories) { category ->
                    AdvancedEntityItem(
                        modifier = Modifier
                            .padding(
                                vertical = 4.dp
                            )
                            .clip(MaterialTheme.shapes.medium)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.secondary,
                                shape = MaterialTheme.shapes.medium
                            )
                            .background(
                                MaterialTheme.colorScheme.surface
                            ),
                        icon = category.categoryIcon,
                        iconModifier = Modifier
                            .size(
                                40.dp
                            )
                            .clip(
                                shape = MaterialTheme.shapes.extraLarge
                            ),
                        iconDescription = category.categoryIconDescription,
                        menuItems = listOf(
                            ActionWithName(
                                name = "Delete Category",
                                action = {
                                    onEvent(
                                        CategoryEvent.DeleteCategory(
                                            category
                                        )
                                    )
                                },
                            ), ActionWithName(
                                name = "Edit Category",
                                action = {
                                    onEvent(
                                        CategoryEvent.ShowDialog(
                                            category
                                        )
                                    )
                                },
                            )
                        )
                    ) {
                        Text(
                            text = category.categoryName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
