package com.fredy.mysavings.ui.Screens.Record

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Feature.Data.Database.Model.TrueRecord
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Util.BalanceColor
import com.fredy.mysavings.Util.formatBalanceAmount
import com.fredy.mysavings.Util.isTransfer
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SimpleEntityItem

@Composable
fun RecordEntityItem(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    onBackgroundColor: Color,
    item: TrueRecord,
    onItemClick: () -> Unit,
) {
    SimpleEntityItem(
        modifier = modifier
            .background(
                backgroundColor
            )
            .clickable {
                onItemClick()
            },
        icon = item.toCategory.categoryIcon,
        iconModifier = Modifier
            .size(
                40.dp
            )
            .clip(
                shape = CircleShape
            ),
        iconDescription = item.toCategory.categoryIconDescription,
        endContent = {
            Text(
                text = formatBalanceAmount(
                    amount = item.record.recordAmount,
                    currency = item.record.recordCurrency,
                    isShortenToChar = true,
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
        },
    ) {
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
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SimpleEntityItem(
                modifier = Modifier,
                icon = item.fromWallet.walletIcon,
                iconModifier = Modifier
                    .size(
                        25.dp
                    )
                    .clip(
                        shape = MaterialTheme.shapes.small
                    ),
                iconDescription = item.fromWallet.walletIconDescription,
                contentWeight = 0f
            ) {
                Text(
                    text = item.fromWallet.walletName,
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
            if (isTransfer(item.record.recordType)) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    tint = onBackgroundColor,
                    contentDescription = ""
                )
                SimpleEntityItem(
                    modifier = Modifier,
                    icon = item.toWallet.walletIcon,
                    iconModifier = Modifier
                        .size(
                            25.dp
                        )
                        .clip(
                            shape = MaterialTheme.shapes.small
                        ),
                    iconDescription = item.toWallet.walletIconDescription,
                    contentWeight = 0f
                ) {
                    Text(
                        text = item.toWallet.walletName,
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