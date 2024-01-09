package com.fredy.mysavings.ui.Screens.Account

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
import com.fredy.mysavings.ViewModels.AccountState
import com.fredy.mysavings.ui.Screens.ZCommonComponent.CustomStickyHeader
import com.fredy.mysavings.ui.Screens.ZCommonComponent.DetailAppBar
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SimpleEntityItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AccountDetailSheet(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onSurface,
    state: AccountState,
    onBackIconClick: () -> Unit,
) {
    DetailAppBar(
        title = "Account details",
        icon = state.account.accountIcon,
        iconDescription = state.account.accountIconDescription,
        itemName = state.account.accountName,
        itemInfo = formatBalanceAmount(
            state.account.accountAmount,
            state.accountCurrency,
            true
        ),
        onNavigationIconClick = onBackIconClick,
    ) {
        Row(modifier = Modifier.padding(bottom = 8.dp)) {
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
                        modifier = Modifier.padding(vertical = 4.dp),
                        iconModifier = Modifier
                            .size(
                                40.dp
                            )
                            .clip(
                                shape = CircleShape
                            ),
                        icon = item.toCategory.categoryIcon,
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
                            text = item.toCategory.categoryName,
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