package com.fredy.mysavings.ui.Screens.Category

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Util.formatBalanceAmount
import com.fredy.mysavings.Util.formatDay
import com.fredy.mysavings.Util.formatTime
import com.fredy.mysavings.ViewModels.CategoryState
import com.fredy.mysavings.ui.Screens.ZCommonComponent.CustomStickyHeader
import com.fredy.mysavings.ui.Screens.ZCommonComponent.DetailAppBar
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SimpleEntityItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryDetailSheet(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onSurface,
    state: CategoryState,
    onBackIconClick: () -> Unit,
) {
    DetailAppBar(
        title = "Category details",
        icon = state.category.categoryIcon,
        iconDescription = state.category.categoryIconDescription,
        itemName = state.category.categoryName,
        itemInfo = state.category.categoryType.name,
        onNavigationIconClick = onBackIconClick,
    ) {
        Row {
            Text(text = "Total of: " + state.trueRecordMaps.sumOf { it.records.size } + " records")
        }
        LazyColumn(
            modifier = modifier.padding(
                bottom = 16.dp, end = 8.dp
            )
        ) {
            state.trueRecordMaps.forEach { trueRecordMap ->
                stickyHeader {
                    CustomStickyHeader(
                        modifier = Modifier.background(
                            backgroundColor
                        ),
                        title = formatDay(
                            trueRecordMap.recordDate
                        ),
                        textStyle = MaterialTheme.typography.titleMedium
                    )
                }
                items(trueRecordMap.records,key = {it.record.recordId}) { item ->

                    Divider(
                        modifier = Modifier.height(
                            0.3.dp
                        ),
                        color = onBackgroundColor.copy(
                            alpha = 0.4f
                        )
                    )
                    SimpleEntityItem(
                        modifier = Modifier.padding(
                            vertical = 4.dp
                        ),
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
                            text = item.fromAccount.accountName,
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
                            color = onBackgroundColor,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}