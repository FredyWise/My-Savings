package com.fredy.mysavings.Feature.Presentation.Screens.AddRecord.AddBulk

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import com.fredy.mysavings.Feature.Presentation.Util.isExpense
import com.fredy.mysavings.Feature.Presentation.Util.isTransfer

@Composable
fun RecordList(
    modifier: Modifier = Modifier,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onBackground,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    enterTransition: EnterTransition = fadeIn(),
    exitTransition: ExitTransition = fadeOut(),
    records: List<Record>?,
    onItemClick: (Record) -> Unit,
) {
    val key = records.hashCode()
    val isVisible = remember(key) {
        MutableTransitionState(
            false
        ).apply { targetState = true }
    }
    AnimatedVisibility(
        visibleState = isVisible,
        enter = enterTransition,
        exit = exitTransition,
    ) {
        if (!records.isNullOrEmpty()) {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
            ) {
                items(records) { item ->
                    val itemDescriptionRegex = "Item Description: (.*?)\\n".toRegex()
                    val matchResult = itemDescriptionRegex.find(item.recordNotes)
                    val nameText = matchResult?.groups?.get(1)?.value ?: item.recordNotes
                    SimpleEntityItem(
                        modifier = modifier
                            .background(
                                backgroundColor
                            )
                            .clickable {
                                onItemClick(item)
                            }
                            .padding(vertical = 6.dp),
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
                                ),
                                modifier = Modifier.padding(
                                    end = 10.dp
                                ),
                                textAlign = TextAlign.End
                            )
                        },
                    ) {
                        Text(
                            text = nameText,
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
                    Divider(
                        modifier = Modifier.height(0.3.dp),
                        color = onBackgroundColor.copy(
                            alpha = 0.4f
                        )
                    )
                }
                item {
                    val firstRecord = records.first()
                    val totalAmount =
                        records.sumOf { if (isExpense(it.recordType)) it.recordAmount else 0.0 }
                    SimpleEntityItem(
                        modifier = modifier
                            .background(
                                backgroundColor
                            )
                            .clickable {
                                onItemClick(Record())
                            }
                            .padding(vertical = 6.dp),
                    ) {
                        Text(
                            text = "Add New Record (+)",
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
                    Divider(
                        modifier = Modifier
                            .height(
                                2.dp
                            )
                            .padding(start = 4.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    SimpleEntityItem(
                        modifier = modifier
                            .padding(vertical = 6.dp)
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

        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(
                        40.dp
                    ),
                    strokeWidth = 4.dp,
                    color = MaterialTheme.colorScheme.onBackground
                )

            }
        }
    }
}

