package com.fredy.mysavings.ui.Screens.Category

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Data.Database.Model.Category
import com.fredy.mysavings.Util.ActionWithName
import com.fredy.mysavings.Util.isTransfer
import com.fredy.mysavings.ViewModels.CategoryMap
import com.fredy.mysavings.ViewModels.Event.CategoryEvent
import com.fredy.mysavings.ViewModels.Event.RecordsEvent
import com.fredy.mysavings.ui.Screens.ZCommonComponent.AdvancedEntityItem
import com.fredy.mysavings.ui.Screens.ZCommonComponent.CustomStickyHeader
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SimpleWarningDialog

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryBody(
    modifier: Modifier = Modifier,
    categoryMaps: List<CategoryMap>,
    onEvent: (CategoryEvent) -> Unit,
    onEntityClick: () -> Unit,
) {
    var isShowWarning by remember { mutableStateOf(false) }
    var tempCategory by remember { mutableStateOf(Category()) }
    SimpleWarningDialog(
        isShowWarning = isShowWarning,
        onDismissRequest = { isShowWarning = false },
        onSaveClicked = {
            onEvent(
                CategoryEvent.DeleteCategory(
                    tempCategory
                )
            )
        },
        warningText = "Are You Sure Want to Delete This Category?"
    )
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
                items(categoryMap.categories,key = {it.categoryId}) { category ->
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
                            )
                            .clickable {
                                onEntityClick()
                                onEvent(
                                    CategoryEvent.GetCategoryDetail(
                                        category
                                    )
                                )
                            },
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
                                    isShowWarning = true
                                    tempCategory = category
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
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(75.dp))
        }
    }
}
