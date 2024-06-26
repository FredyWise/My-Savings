package com.fredy.mysavings.ui.Screens.AddRecord

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fredy.mysavings.Data.RoomDatabase.Entity.Account
import com.fredy.mysavings.Data.RoomDatabase.Entity.Category
import com.fredy.mysavings.Data.RoomDatabase.Enum.RecordType
import com.fredy.mysavings.Util.formatBalanceAmount
import com.fredy.mysavings.R
import com.fredy.mysavings.ViewModel.CategoryMap
import com.fredy.mysavings.ui.Screens.SimpleButton
import com.fredy.mysavings.ui.Screens.SimpleEntityItem

@Composable
fun AccountBottomSheet(
    modifier: Modifier = Modifier,
    accounts: List<Account>,
    onSelectAccount: (Account) -> Unit,
    onAddAccount: () -> Unit
) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 8.dp
            ),
        text = "Select a Account",
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.headlineSmall
    )
    LazyColumn(modifier = modifier) {
        items(accounts) { account ->
            SimpleEntityItem(
                modifier = Modifier
                    .clickable {
                        onSelectAccount(account)
                    }
                    .padding(
                        vertical = 4.dp
                    ),
                icon = account.accountIcon,
                iconDescription = account.accountIconDescription,
                iconModifier = Modifier
                    .size(65.dp)
                    .clip(
                        shape = MaterialTheme.shapes.medium
                    ),
                endContent = {
                    Text(
                        text = formatBalanceAmount(
                            amount = account.accountAmount,
                            currency = account.accountCurrency
                        ),
                        style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
            ) {
                Text(
                    text = account.accountName,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                )
            }
        }
        item {
            SimpleButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 70.dp, vertical = 8.dp
                    )
                    .clip(
                        MaterialTheme.shapes.medium
                    )
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.secondary,
                        shape = MaterialTheme.shapes.medium
                    ),
                image = R.drawable.ic_add_foreground,
                onClick = {
                    onAddAccount()
                },
                title = "Add Account",
            )
        }
    }

}

@Composable
fun CategoryBottomSheet(
    modifier: Modifier = Modifier,
    categoryMaps: List<CategoryMap>,
    recordType: RecordType,
    onSelectCategory: (Category) -> Unit,
    onAddCategory: () -> Unit
) {
    val categoryMap = categoryMaps.find { c ->
        c.categoryType == recordType
    } ?: CategoryMap()
    Column {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 8.dp
                ),
            text = "Select a Category",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall
        )
        LazyVerticalGrid(
            modifier = modifier,
            columns = GridCells.Fixed(3)
        ) {

            items(categoryMap.categories) { category ->
                Column(modifier = Modifier
                    .clickable {
                        onSelectCategory(
                            category
                        )
                    }
                    .padding(
                        vertical = 4.dp
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center) {
                    Icon(
                        modifier = Modifier
                            .size(
                                65.dp
                            )
                            .clip(
                                shape = MaterialTheme.shapes.extraLarge
                            ),
                        painter = painterResource(
                            category.categoryIcon
                        ),
                        contentDescription = category.categoryIconDescription,
                        tint = Color.Unspecified
                    )
                    Text(
                        text = category.categoryName,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .clickable {
                            onAddCategory()
                        }
                        .padding(
                            vertical = 4.dp
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        modifier = Modifier
                            .size(
                                65.dp
                            )
                            .clip(
                                shape = MaterialTheme.shapes.extraLarge
                            ),
                        painter = painterResource(
                            R.drawable.ic_add_foreground
                        ),
                        contentDescription = "",
                        tint = Color.Unspecified
                    )
                    Text(
                        text = "Add Category",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

    }

}