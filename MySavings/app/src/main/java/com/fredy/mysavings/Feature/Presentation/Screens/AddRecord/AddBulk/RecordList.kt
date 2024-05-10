package com.fredy.mysavings.Feature.Presentation.Screens.AddRecord.AddBulk

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Domain.Model.Record
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SimpleEntityItem
import com.fredy.mysavings.Feature.Presentation.Util.BalanceColor
import com.fredy.mysavings.Feature.Presentation.Util.formatBalanceAmount
import com.fredy.mysavings.Feature.Presentation.Util.isTransfer

@Composable
fun RecordList(
    modifier: Modifier = Modifier,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onBackground,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    records: List<Record>?,
    onItemClick: (Record) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
    ) {
        records?.let {
            items(records) { item ->
                Divider(
                    modifier = Modifier.height(0.3.dp),
                    color = onBackgroundColor.copy(
                        alpha = 0.4f
                    )
                )

                SimpleEntityItem(
                    modifier = modifier
                        .background(
                            backgroundColor
                        )
                        .clickable {
                            onItemClick(item)
                        },
                    endContent = {
                        Text(
                            text = formatBalanceAmount(
                                amount = item.recordAmount,
                                currency = item.recordCurrency,
                            ),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = BalanceColor(
                                amount = item.recordAmount,
                                isTransfer = isTransfer(
                                    item.recordType
                                )
                            ),
                            modifier = Modifier.padding(
                                end = 10.dp
                            ),
                            textAlign = TextAlign.End
                        )
                    },
                ) {
                    Text(
                        text = if (isTransfer(item.recordType)) {
                            RecordType.Transfer.name
                        } else {
                            val itemDescriptionRegex = "Item Description: (.*?)\\n".toRegex()
                            val matchResult = itemDescriptionRegex.find(item.recordNotes)
                            matchResult?.groups?.get(1)?.value ?: item.recordNotes
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = onBackgroundColor,
                        modifier = Modifier.padding(
                            vertical = 3.dp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            if(records.isNotEmpty()) {
                item {
                    val firstRecord = records.first()
                    val totalAmount = records.sumOf { it.recordAmount }
                    SimpleEntityItem(
                        modifier = modifier
                            .background(
                                backgroundColor
                            ),
                        endContent = {
                            Text(
                                text = formatBalanceAmount(
                                    amount = totalAmount,
                                    currency = firstRecord.recordCurrency,
                                ),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = BalanceColor(
                                    amount = totalAmount,
                                    isTransfer = isTransfer(
                                        firstRecord.recordType
                                    )
                                ),
                                modifier = Modifier.padding(
                                    end = 10.dp
                                ),
                                textAlign = TextAlign.End
                            )
                        },
                    ) {
                        Text(
                            text = "Total: ",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = onBackgroundColor,
                            modifier = Modifier.padding(
                                vertical = 3.dp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }

}