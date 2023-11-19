package com.fredy.mysavings.ui.Screens.Record

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Util.BalanceColor
import com.fredy.mysavings.Data.RoomDatabase.Enum.RecordType
import com.fredy.mysavings.Data.RoomDatabase.Event.RecordsEvent
import com.fredy.mysavings.Util.formatBalanceAmount
import com.fredy.mysavings.Util.formatDay
import com.fredy.mysavings.Util.isTransfer
import com.fredy.mysavings.ViewModel.RecordMap
import com.fredy.mysavings.ui.Screens.CustomStickyHeader
import com.fredy.mysavings.ui.Screens.SimpleEntityItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecordBody(
    modifier: Modifier = Modifier,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onBackground,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    trueRecords: List<RecordMap>,
    onEvent: (RecordsEvent) -> Unit,
) {
    LazyColumn(modifier) {
        trueRecords.forEach { trueRecord ->
            stickyHeader {
                CustomStickyHeader(
                    modifier = Modifier.background(
                        MaterialTheme.colorScheme.background
                    ),
                    title = formatDay(
                        trueRecord.recordDate
                    ),
                    textStyle = MaterialTheme.typography.titleMedium
                )
            }
            items(trueRecord.records) { item ->
                Divider(
                    modifier = Modifier.height(0.3.dp),
                    color = onBackgroundColor.copy(
                        alpha = 0.4f
                    )
                )
                SimpleEntityItem(modifier = Modifier
                    .background(
                        backgroundColor
                    )
                    .padding(bottom = 5.dp)
                    .clickable {
                        onEvent(
                            RecordsEvent.ShowDialog(
                                item
                            )
                        )
                    },
                    icon = item.toCategory.categoryIcon,
                    iconModifier = Modifier
                        .size(
                            40.dp
                        )
                        .clip(
                            shape = MaterialTheme.shapes.extraLarge
                        ),
                    iconDescription = item.toCategory.categoryIconDescription,
                    endContent = {
                        Text(
                            text = formatBalanceAmount(
                                amount = item.record.recordAmount,
                                currency = item.record.recordCurrency
                            ),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = BalanceColor(
                                amount = item.record.recordAmount,
                                isTransfer = isTransfer(
                                    item.record.recordType
                                )
                            ),
                            modifier = Modifier.padding(
                                end = 10.dp
                            ),
                            textAlign = TextAlign.End
                        )
                    }) {
                    Text(
                        text = if (isTransfer(item.record.recordType)) {
                            RecordType.Transfer.name
                        } else {
                            item.toCategory.categoryName
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = onBackgroundColor,
                        modifier = Modifier.padding(
                            vertical = 3.dp
                        ),
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SimpleEntityItem(
                            modifier = Modifier.weight(
                                1f
                            ),
                            icon = item.fromAccount.accountIcon,
                            iconModifier = Modifier
                                .size(
                                    25.dp
                                )
                                .clip(
                                    shape = MaterialTheme.shapes.small
                                ),
                            iconDescription = item.fromAccount.accountIconDescription
                        ) {
                            Text(
                                text = item.fromAccount.accountName,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = onBackgroundColor,
                                modifier = Modifier.padding(
                                    vertical = 3.dp
                                ),
                                maxLines = 1,
                            )
                        }
                        if (isTransfer(item.record.recordType)) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                tint = onBackgroundColor,
                                contentDescription = ""
                            )
                            SimpleEntityItem(
                                modifier = Modifier.weight(
                                    1f
                                ),
                                icon = item.toAccount.accountIcon,
                                iconModifier = Modifier
                                    .size(
                                        25.dp
                                    )
                                    .clip(
                                        shape = MaterialTheme.shapes.small
                                    ),
                                iconDescription = item.toAccount.accountIconDescription
                            ) {
                                Text(
                                    text = item.toAccount.accountName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = onBackgroundColor,
                                    modifier = Modifier.padding(
                                        vertical = 3.dp
                                    ),
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
