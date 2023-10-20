package com.fredy.mysavings.ui.component.Records

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Data.Balance
import com.fredy.mysavings.Data.FormatBalanceAmount
import com.fredy.mysavings.Data.Records.Record
import com.fredy.mysavings.Data.User.Account
import com.fredy.mysavings.Data.User.Category
import com.fredy.mysavings.Data.User.accountIcons
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategorizedLazyColumn(
    records: List<Record>,
    modifier: Modifier = Modifier,
    formatDate: (LocalDate) -> String,
) {
    LazyColumn(modifier) {
        records.forEach { record ->
            stickyHeader {
                RecordHeader(
                    date = formatDate(
                        record.date
                    )
                )
            }
            items(record.items) { item ->
                RecordItem(
                    account = item.account,
                    toAccount = item.toAccount,
                    toCategory = item.toCategory,
                    balance = item.balance
                )
            }
        }
    }
}

@Composable
fun RecordHeader(
    modifier: Modifier = Modifier, date: String
) {
    Text(
        text = date,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.background
            )
            .padding(
                8.dp
            )
            .padding(top = 20.dp),
    )
    Divider(
        modifier = Modifier.height(2.dp),
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun RecordItem(
    modifier: Modifier = Modifier,
    account: Account,
    toAccount: Account?,
    toCategory: Category?,
    balance: Balance,
) {
    Divider(
        modifier = Modifier.height(0.3.dp),
        color = MaterialTheme.colorScheme.onBackground.copy(
            alpha = 0.4f
        )
    )
    Row(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.background
            )
            .padding(bottom = 5.dp),
        verticalAlignment = Alignment.CenterVertically,

        ) {
        ChooseIcon(
            modifier = Modifier.weight(1f),
            toCategory = toCategory,
            toAccount = toAccount,
        )
        Column(
            modifier = Modifier
                .padding(1.dp)
                .weight(
                    3.5f
                ),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = if (toCategory != null) {
                    toCategory!!.name
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
            TransferOrNormalIcon(
                account = account,
                toAccount = toAccount
            )
        }
        Text(
            text = FormatBalanceAmount(balance = balance),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = balance.balanceColor,
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
                account.icon
            ),
            contentDescription = account.iconDescription,
            tint = account.iconColor,
            modifier = Modifier.size(
                width = 20.dp, height = 20.dp
            )
        )
        Text(
            text = account.name,
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
                    it.icon
                ),
                contentDescription = it.iconDescription,
                tint = it.iconColor,
                modifier = Modifier.size(
                    width = 20.dp, height = 20.dp
                )
            )
            Text(
                text = it.name,
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
            painter = painterResource(it.icon),
            contentDescription = it.iconDescription,
            tint = it.iconColor,
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
                accountIcons[0]
            ),
            contentDescription = it.iconDescription,
            tint = it.iconColor,
            modifier = Modifier
                .size(
                    width = 40.dp, height = 40.dp
                )
                .then(modifier),
        )
    }
}
