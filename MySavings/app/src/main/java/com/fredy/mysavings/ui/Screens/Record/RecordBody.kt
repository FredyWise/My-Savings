package com.fredy.mysavings.ui.Screens.Record

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Data.BalanceColor
import com.fredy.mysavings.Data.formatBalanceAmount
import com.fredy.mysavings.Data.RoomDatabase.Dao.TrueRecord
import com.fredy.mysavings.Data.RoomDatabase.Entity.Account
import com.fredy.mysavings.Data.RoomDatabase.Entity.Category
import com.fredy.mysavings.Data.RoomDatabase.Event.RecordsEvent
import com.fredy.mysavings.Data.formatDate
import com.fredy.mysavings.ViewModel.RecordMap
import com.fredy.mysavings.ui.CustomStickyHeader
import com.fredy.mysavings.ui.SimpleEntityItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecordBody(
    modifier: Modifier = Modifier,
    trueRecords: List<RecordMap>,
    onEvent: (RecordsEvent) -> Unit,
) {
    LazyColumn(modifier) {
        trueRecords.forEach { trueRecord ->
            stickyHeader {
                CustomStickyHeader(
                    title = formatDate(
                        trueRecord.recordDateTime.toLocalDate()
                    ),
                    textStyle = MaterialTheme.typography.titleMedium
                )
            }
            items(trueRecord.records) { item ->
                Divider(
                    modifier = Modifier.height(0.3.dp),
                    color = MaterialTheme.colorScheme.onBackground.copy(
                        alpha = 0.4f
                    )
                )
                SimpleEntityItem(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.background
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
                        .size(40.dp)
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
                                isTransfer = item.record.isTransfer
                            ),
                            modifier = Modifier
                                .padding(end = 10.dp),
                            textAlign = TextAlign.End
                        )
                    }
                ){
                    Text(
                        text = if (item.toCategory != null) {
                            item.toCategory!!.categoryName
                        } else {
                            "Transfer"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(
                            vertical = 3.dp
                        ),
                    )
                }
            }
        }
    }
}

@Composable
fun RecordItem(
    modifier: Modifier = Modifier,
    item: TrueRecord,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,

        ) {
//        ChooseIcon(
//            modifier = Modifier.weight(1f),
//            toCategory = item.toCategory,
//            toAccount = item.toAccount,
//        )
        Column(
            modifier = Modifier
                .padding(1.dp)
                .weight(
                    3.5f
                ),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = if (item.toCategory != null) {
                    item.toCategory!!.categoryName
                } else {
                    "Transfer"
                },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(
                    vertical = 3.dp
                ),
            )
//            TransferOrNormalIcon(
//                account = item.fromAccount,
//                toAccount = item.toAccount
//            )
        }
        Text(
            text = formatBalanceAmount(
                amount = item.record.recordAmount,
                currency = item.record.recordCurrency
            ),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = BalanceColor(
                amount = item.record.recordAmount,
                isTransfer = item.record.isTransfer
            ),
            modifier = Modifier
                .padding(end = 10.dp)
                .weight(
                    2.5f
                ),
            textAlign = TextAlign.End
        )
    }

}

@Composable
fun TransferOrNormalIcon(
    modifier: Modifier = Modifier,
    account: Account,
    toAccount: Account? = null,
) {
    Row(modifier = modifier) {
        Icon(
            painter = painterResource(
                account.accountIcon
            ),
            contentDescription = account.accountIconDescription,
            modifier = Modifier.size(
                width = 20.dp, height = 20.dp
            )
        )
        Text(
            text = account.accountName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground.copy(
                alpha = 0.5f
            ),
        )
        toAccount?.let {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(
                    width = 20.dp, height = 20.dp
                )
            )
            Icon(
                painter = painterResource(
                    it.accountIcon
                ),
                contentDescription = it.accountIconDescription,
                modifier = Modifier.size(
                    width = 20.dp, height = 20.dp
                )
            )
            Text(
                text = it.accountName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground.copy(
                    alpha = 0.5f
                ),
            )
        }
    }
}

@Composable
fun ChooseIcon(
    modifier: Modifier = Modifier,
    toCategory: Category?,
    toAccount: Account?
) {
    toCategory?.let {
        Icon(
            painter = painterResource(it.categoryIcon),
            contentDescription = it.categoryIconDescription,
            modifier = Modifier
                .size(
                    width = 40.dp, height = 40.dp
                )
                .then(modifier),
        )
    }
    toAccount?.let {
        Icon(
            painter = painterResource(
//                accountIcons[0]
                it.accountIcon
            ),
            contentDescription = it.accountIconDescription,
            modifier = Modifier
                .size(
                    width = 40.dp, height = 40.dp
                )
                .then(modifier),
        )
    }
}
