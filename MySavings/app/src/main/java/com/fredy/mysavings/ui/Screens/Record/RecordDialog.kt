package com.fredy.mysavings.ui.Screens.Record

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.fredy.mysavings.Data.Database.Model.Record
import com.fredy.mysavings.Data.Database.Model.TrueRecord
import com.fredy.mysavings.Util.BalanceColor
import com.fredy.mysavings.Util.formatDateTime
import com.fredy.mysavings.Util.isTransfer
import com.fredy.mysavings.ViewModels.Event.RecordsEvent
import com.fredy.mysavings.ui.Screens.ZCommonComponent.BalanceItem
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SimpleEntityItem
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SimpleWarningDialog

@Composable
fun RecordDialog(
    modifier: Modifier = Modifier,
    onSurface: Color = MaterialTheme.colorScheme.onBackground,
    surface: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
    background: Color = MaterialTheme.colorScheme.background,
    onAmountBox: Color = MaterialTheme.colorScheme.onSecondary,
    onEdit: () -> Unit,
    trueRecord: TrueRecord,
    balanceColor: Color = BalanceColor(
        amount = trueRecord.record.recordAmount,
        isTransfer = isTransfer(
            trueRecord.record.recordType
        )
    ).copy(alpha = 0.9f),
    onDismissDialog: () -> Unit,
    onSaveClicked: (record: Record) -> Unit,
) {
    var isShowWarning by remember { mutableStateOf(false) }
    SimpleWarningDialog(
        isShowWarning = isShowWarning,
        onDismissRequest = { isShowWarning = false },
        onSaveClicked = {
            onSaveClicked(trueRecord.record)
            onDismissDialog()
        },
        warningText = "Are You Sure Want to Delete This Record?"
    )
    Dialog(onDismissRequest = onDismissDialog) {
        Column(
            modifier = modifier
                .padding(
                    vertical = 4.dp
                )
                .clip(MaterialTheme.shapes.medium)
                .background(background)
                .border(
                    width = 2.dp,
                    color = balanceColor,
                    shape = MaterialTheme.shapes.medium
                ),
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = balanceColor
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
                                    onDismissDialog()
                                }
                                .padding(4.dp),
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "",
                            tint = onAmountBox,
                        )
                        Spacer(
                            modifier = Modifier.weight(
                                1f
                            ),
                        )
                        Icon(
                            modifier = Modifier
                                .clip(
                                    MaterialTheme.shapes.large
                                )
                                .clickable {
                                    isShowWarning = true
                                }
                                .padding(4.dp),
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "",
                            tint = onAmountBox,
                        )
                        Icon(
                            modifier = Modifier
                                .clip(
                                    MaterialTheme.shapes.large
                                )
                                .clickable {
                                    onDismissDialog()
                                    onEdit()
                                }
                                .padding(4.dp),
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "",
                            tint = onAmountBox,
                        )
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        BalanceItem(
                            title = trueRecord.toCategory.categoryType.name,
                            titleColor = onAmountBox,
                            amount = trueRecord.record.recordAmount,
                            amountColor = onAmountBox,
                            currency = trueRecord.record.recordCurrency,
                            titleStyle = MaterialTheme.typography.titleLarge,
                            amountStyle = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = formatDateTime(
                                trueRecord.record.recordDateTime
                            ),
                            textAlign = TextAlign.End,
                            color = onAmountBox
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .background(
                        color = surface
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
                                )
                            ) "From:    " else "Account:    ",
                            color = onSurface,
                            style = MaterialTheme.typography.titleLarge,
                        )
                        SimpleEntityItem(
                            modifier = Modifier
                                .padding(4.dp)
                                .clip(
                                    MaterialTheme.shapes.medium
                                )
                                .border(
                                    width = 2.dp,
                                    color = balanceColor,
                                    shape = MaterialTheme.shapes.medium
                                )
                                .background(
                                    surface
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
                            Text(
                                text = trueRecord.fromAccount.accountName,
                                color = onSurface
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 8.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isTransfer(trueRecord.record.recordType)) "To:   " else "Category:   ",
                            color = onSurface,
                            style = MaterialTheme.typography.titleLarge,
                        )
                        SimpleEntityItem(
                            modifier = Modifier
                                .padding(4.dp)
                                .clip(
                                    MaterialTheme.shapes.medium
                                )
                                .border(
                                    width = 2.dp,
                                    color = balanceColor,
                                    shape = MaterialTheme.shapes.medium
                                )
                                .background(
                                    surface
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
                            Text(
                                text = trueRecord.toCategory.categoryName,
                                color = onSurface
                            )
                        }
                    }
                    Text(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .fillMaxWidth()
                            .padding(
                                vertical = 8.dp
                            ),
                        text = trueRecord.record.recordNotes,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = onSurface
                    )
                }
            }
        }
    }
}