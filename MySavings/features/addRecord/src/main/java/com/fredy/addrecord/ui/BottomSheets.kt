package com.fredy.addrecord.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
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
import com.fredy.domain.enums.RecordType
import com.fredy.domain.enumsChecker.isTransfer
import com.fredy.domain.model.Category
import com.fredy.domain.model.Wallet
import com.fredy.domain.util.SavingsIcons.AddCircleOutlineIcon
import com.fredy.domain.util.SavingsIcons.savingsIcons
import com.fredy.domain.util.resource.DataError
import com.fredy.domain.util.resource.Resource
import com.fredy.theme.util.BalanceColor
import com.fredy.theme.util.formatBalanceAmount
import com.fredy.ui.components.button.SimpleButton
import com.fredy.ui.components.handler.ResourceHandler
import com.fredy.ui.components.list.SimpleEntityItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    onDismissModal: (Boolean) -> Unit,
    isAccount: Boolean,
    recordType: RecordType,
    walletResource: Resource<List<Wallet>, DataError.Local>,
    categoryResource: Resource<List<Category>, DataError.Local>,
    showWalletDialog: (wallet: Wallet) -> Unit,
    showCategoryDialog: (category: Category) -> Unit,
    onSelectFromAccount: (Wallet) -> Unit,
    onSelectToAccount: (Wallet) -> Unit,
    onSelectCategory: (Category) -> Unit,
) {
    ModalBottomSheet(
        modifier = modifier.padding(top = 24.dp),
        sheetState = sheetState,
        dragHandle = {},
        onDismissRequest = {
            onDismissModal(false)
        },
    ) {
        if (isAccount || recordType.isTransfer()) {
            walletResource.let { resource ->
                ResourceHandler(
                    modifier = if (resource is Resource.Loading || (resource as Resource.Success).data.isEmpty()) Modifier.fillMaxHeight(
                        0.5f
                    ) else Modifier,
                    resource = resource,
                    nullOrEmptyMessage = "You Didn't Have Any Account Yet",
                    isNullOrEmpty = { it.isNullOrEmpty() },
                    onMessageClick = {
                        showWalletDialog(Wallet(walletName = ""))
                    },
                ) { data ->
                    if (isAccount) {
                        AccountBottomSheet(
                            wallets = data,
                            onSelectAccount = {
                                onSelectFromAccount(it)
                                onDismissModal(false)
                            },
                            onAddAccount = {
                                showWalletDialog(Wallet(walletName = ""))
                            },
                        )
                    } else if (recordType.isTransfer()) {
                        AccountBottomSheet(
                            wallets = data,
                            onSelectAccount = {
                                onSelectToAccount(it)
                                onDismissModal(false)
                            },
                            onAddAccount = {
                                showWalletDialog(Wallet(walletName = ""))
                            },
                        )
                    }
                }
            }
        } else {
            categoryResource.let { resource ->
                ResourceHandler(
                    modifier = if (resource is Resource.Loading || (resource as Resource.Success).data.isEmpty()) Modifier.fillMaxHeight(
                        0.5f
                    ) else Modifier,
                    resource = resource,
                    nullOrEmptyMessage = "You Didn't Have Any Categories Yet",
                    isNullOrEmpty = { it.isNullOrEmpty() },
                    onMessageClick = {
                        showCategoryDialog(Category(categoryName = ""))
                    },
                ) { data ->
                    if (!recordType.isTransfer()) {
                        CategoryBottomSheet(
                            categoryList = data,
                            recordType = recordType,
                            onSelectCategory = {
                                onSelectCategory(it)
                                onDismissModal(false)
                            },
                            onAddCategory = {
                                showCategoryDialog(Category(categoryName = ""))
                            },
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun AccountBottomSheet(
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    wallets: List<Wallet>,
    onSelectAccount: (Wallet) -> Unit,
    onAddAccount: () -> Unit
) {
    Column(Modifier.background(backgroundColor)) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 8.dp
                ),
            text = "Select an Account",
            color = textColor,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall
        )
        LazyColumn(
            modifier = modifier.padding(
                horizontal = 8.dp
            )
        ) {
            items(wallets, key = { it.walletId }) { account ->
                SimpleEntityItem(
                    modifier = Modifier
                        .clickable {
                            onSelectAccount(
                                account
                            )
                        }
                        .padding(
                            vertical = 4.dp
                        ),
                    icon = account.walletIcon,
                    iconDescription = account.walletIconDescription,
                    iconModifier = Modifier
                        .size(
                            65.dp
                        )
                        .clip(
                            shape = MaterialTheme.shapes.medium
                        ),
                    endContent = {
                        Text(
                            text = formatBalanceAmount(
                                amount = account.walletAmount,
                                currency = account.walletCurrency
                            ),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontSize = 20.sp
                            ),
                            color = BalanceColor(
                                amount = account.walletAmount
                            ),
                        )
                    },
                ) {
                    Text(
                        text = account.walletName,
                        style = MaterialTheme.typography.headlineSmall,
                        color = textColor,
                        maxLines = 2,
                    )
                }
            }
            item {
                SimpleButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 70.dp,
                            vertical = 8.dp
                        )
                        .clip(
                            MaterialTheme.shapes.medium
                        )
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.secondary,
                            shape = MaterialTheme.shapes.medium
                        ),
                    image = AddCircleOutlineIcon.image,
                    onClick = {
                        onAddAccount()
                    },
                    title = "Add Account",
                    titleColor = textColor
                )
                Spacer(modifier = Modifier.height(200.dp))
            }
        }
    }

}

@Composable
fun CategoryBottomSheet(
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    categoryList: List<Category>,
    recordType: RecordType,
    onSelectCategory: (Category) -> Unit,
    onAddCategory: () -> Unit
) {
    val categories = categoryList.filter { c ->
        c.categoryType == recordType
    }
    Column(Modifier.background(backgroundColor)) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 8.dp
                ),
            text = "Select a Category",
            color = textColor,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall
        )
        LazyVerticalGrid(
            modifier = modifier.padding(horizontal = 8.dp),
            columns = GridCells.Fixed(3)
        ) {

            items(categories) { category ->
                Column(
                    modifier = Modifier
                        .clickable {
                            onSelectCategory(
                                category
                            )
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
                            id = savingsIcons[category.categoryIconDescription]?.image
                                ?: category.categoryIcon
                        ),
                        contentDescription = category.categoryIconDescription,
                        tint = Color.Unspecified
                    )
                    Text(
                        text = category.categoryName,
                        style = MaterialTheme.typography.titleLarge,
                        color = textColor,
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
                            AddCircleOutlineIcon.image
                        ),
                        contentDescription = "",
                        tint = Color.Unspecified
                    )
                    Text(
                        text = "Add Category",
                        style = MaterialTheme.typography.titleLarge,
                        color = textColor,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(200.dp))
    }

}