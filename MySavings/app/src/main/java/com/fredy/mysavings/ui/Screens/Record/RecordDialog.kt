package com.fredy.mysavings.ui.Screens.Record

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.fredy.mysavings.Data.BalanceColor
import com.fredy.mysavings.Data.RoomDatabase.Dao.TrueRecord
import com.fredy.mysavings.Data.RoomDatabase.Enum.RecordType
import com.fredy.mysavings.Data.RoomDatabase.Event.RecordsEvent
import com.fredy.mysavings.Data.formatDateTime
import com.fredy.mysavings.Data.isTransfer
import com.fredy.mysavings.ui.Screens.BalanceItem
import com.fredy.mysavings.ui.Screens.SimpleEntityItem

@Composable
fun RecordDialog(
    trueRecord: TrueRecord,
    onEvent: (RecordsEvent) -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = {
        onEvent(
            RecordsEvent.HideDialog
        )
    }) {
        Column(
            modifier = modifier
                .padding(
                    vertical = 4.dp
                )
                .clip(MaterialTheme.shapes.medium)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    shape = MaterialTheme.shapes.medium
                ),
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = BalanceColor(
                            amount = trueRecord.record.recordAmount,
                            isTransfer = isTransfer(
                                trueRecord.record.recordType
                            )
                        )
                    )
                    .padding(8.dp)
            ) {
                Column {
                    Row {
                        Icon(
                            modifier = Modifier
                                .clip(
                                    MaterialTheme.shapes.large
                                )
                                .clickable {
                                    onEvent(
                                        RecordsEvent.HideDialog
                                    )
                                }
                                .padding(4.dp),
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "",
                        )
                        Divider(
                            modifier = Modifier.weight(
                                1f
                            ),
                            color = Color.Unspecified
                        )
                        Icon(
                            modifier = Modifier
                                .clip(
                                    MaterialTheme.shapes.large
                                )
                                .clickable {
                                    onEvent(
                                        RecordsEvent.DeleteRecord(
                                            trueRecord.record
                                        )
                                    )
                                    onEvent(
                                        RecordsEvent.HideDialog
                                    )
                                }
                                .padding(4.dp),
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "",
                        )
                        Icon(
                            modifier = Modifier
                                .clip(
                                    MaterialTheme.shapes.large
                                )
                                .clickable {
                                    onEvent(
                                        RecordsEvent.HideDialog
                                    )
                                    onEdit()
                                }
                                .padding(4.dp),
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "",
                        )
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        BalanceItem(
                            title = trueRecord.toCategory.categoryType.name,
                            amount = trueRecord.record.recordAmount,
                            amountColor = MaterialTheme.colorScheme.onSurface,
                            currency = trueRecord.record.recordCurrency,
                            titleStyle = MaterialTheme.typography.titleLarge,
                            amountStyle = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = formatDateTime(
                                trueRecord.record.recordDateTime
                            ),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surface
                    )
                    .padding(8.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 8.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isTransfer(
                                    trueRecord.record.recordType
                                )) "From: " else "Account: "
                        )
                        SimpleEntityItem(
                            modifier = Modifier
                                .padding(
                                    vertical = 4.dp
                                )
                                .clip(
                                    MaterialTheme.shapes.medium
                                )
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.secondary,
                                    shape = MaterialTheme.shapes.medium
                                )
                                .background(
                                    MaterialTheme.colorScheme.surface
                                )
                                .padding(8.dp),
                            icon = trueRecord.fromAccount.accountIcon,
                            iconModifier = Modifier
                                .size(
                                    35.dp
                                )
                                .clip(
                                    shape = MaterialTheme.shapes.medium
                                ),
                            iconDescription = trueRecord.fromAccount.accountIconDescription,
                            contentWeight = 0.3f,
                        ) {
                            Text(text = trueRecord.fromAccount.accountName)
                        }
                    }
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 8.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = if (trueRecord.record.recordType == RecordType.Transfer) "To: " else "Category: ")
                        SimpleEntityItem(
                            modifier = Modifier
                                .padding(
                                    vertical = 4.dp
                                )
                                .clip(
                                    MaterialTheme.shapes.medium
                                )
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.secondary,
                                    shape = MaterialTheme.shapes.medium
                                )
                                .background(
                                    MaterialTheme.colorScheme.surface
                                )
                                .padding(8.dp),
                            icon = trueRecord.toCategory.categoryIcon,
                            iconModifier = Modifier
                                .size(
                                    35.dp
                                )
                                .clip(
                                    shape = MaterialTheme.shapes.medium
                                ),
                            iconDescription = trueRecord.toCategory.categoryIconDescription,
                            contentWeight = 0.3f,
                        ) {
                            Text(text = trueRecord.toCategory.categoryName)
                        }
                    }
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = 8.dp
                            ),
                        text = trueRecord.record.recordNotes,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}