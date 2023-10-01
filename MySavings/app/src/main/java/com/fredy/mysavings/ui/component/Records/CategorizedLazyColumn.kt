package com.fredy.mysavings.ui.component.Records

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Data.AccountIcons
import com.fredy.mysavings.Data.CategoryIcons
import com.fredy.mysavings.Data.Records.Item
import com.fredy.mysavings.Data.Records.Record
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategorizedLazyColumn(
    records: List<Record>,
    modifier: Modifier = Modifier,
    formatDate: (LocalDate) -> String
) {
    LazyColumn(modifier){
        records.forEach{ record ->
            stickyHeader { 
                CategoryHeader(date = formatDate(record.date))
            }
            items(record.items) { item ->  
                CategoryItem(item = item)
            }
        }
    }
}

@Composable
fun CategoryHeader(
    date: String, modifier: Modifier = Modifier
) {
    Text(
        text = date,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                8.dp
            )
            .padding(top = 8.dp),
    )
    Divider(
        modifier = Modifier.height(2.dp),
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun CategoryItem(
    item: Item, modifier: Modifier = Modifier
) {
    item.category?.let {
        Row (modifier = modifier){
            Icon(
                painter = CategoryIcons(it.icon),
                contentDescription = it.iconDescription,
                tint = it.iconColor,
                modifier = Modifier.size(
                    width = 40.dp, height = 40.dp
                )
            )
            Row {
                Column {
                    Text(
                        text = it.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                8.dp
                            ),

                        )
                    Row {
                        Icon(
                            painter = AccountIcons(item.account.icon),
                            contentDescription = item.account.iconDescription,
                            tint = item.account.iconColor,
                            modifier = Modifier.size(
                                width = 20.dp,
                                height = 20.dp
                            )
                        )
                        Text(
                            text = item.account.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground.copy(
                                alpha = 0.5f
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            Text(
                text = item.amount.toString() + item.currency,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = item.priceColor.copy(alpha = 0.8f),
                modifier = Modifier.weight(1f)
            )
        }
    }
}
