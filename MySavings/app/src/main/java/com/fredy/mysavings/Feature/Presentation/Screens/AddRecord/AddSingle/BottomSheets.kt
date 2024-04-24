package com.fredy.mysavings.Feature.Presentation.Screens.AddRecord.AddSingle

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
import com.fredy.mysavings.Feature.Domain.Model.Wallet
import com.fredy.mysavings.Feature.Domain.Model.Category
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.R
import com.fredy.mysavings.Feature.Presentation.Util.BalanceColor
import com.fredy.mysavings.Feature.Presentation.Util.formatBalanceAmount
import com.fredy.mysavings.Feature.Presentation.Util.isTransfer
import com.fredy.mysavings.Feature.Presentation.Util.DefaultData.savingsIcons
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.fredy.mysavings.Feature.Presentation.ViewModels.WalletViewModel.WalletState
import com.fredy.mysavings.Feature.Presentation.ViewModels.CategoryViewModel.CategoryMap
import com.fredy.mysavings.Feature.Presentation.ViewModels.CategoryViewModel.CategoryState
import com.fredy.mysavings.Feature.Presentation.ViewModels.WalletViewModel.WalletEvent
import com.fredy.mysavings.Feature.Presentation.ViewModels.CategoryViewModel.CategoryEvent
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.ResourceHandler
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SimpleButton
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SimpleEntityItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    onDismissModal: (Boolean) -> Unit,
    isLeading: Boolean,
    recordType: RecordType,
    walletState: WalletState,
    categoryState: CategoryState,
    onEventAccount: (WalletEvent) -> Unit,
    onEventCategory: (CategoryEvent) -> Unit,
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
        if (isLeading || isTransfer(recordType)) {
            walletState.walletResource.let { resource ->
                ResourceHandler(
                    modifier = if (resource is Resource.Loading || resource.data.isNullOrEmpty()) Modifier.fillMaxHeight(
                        0.5f
                    ) else Modifier,
                    resource = resource,
                    nullOrEmptyMessage = "You Didn't Have Any Account Yet",
                    isNullOrEmpty = { it.isNullOrEmpty() },
                    errorMessage = resource.message ?: "",
                    onMessageClick = {
                        onEventAccount(
                            WalletEvent.ShowDialog(
                                Wallet(
                                    walletName = ""
                                )
                            )
                        )
                    },
                ) { data ->
                    if (isLeading) {
                        AccountBottomSheet(
                            wallets = data,
                            onSelectAccount = {
                                onSelectFromAccount(it)
                                onDismissModal(false)
                            },
                            onAddAccount = {
                                onEventAccount(
                                    WalletEvent.ShowDialog(
                                        Wallet(
                                            walletName = ""
                                        )
                                    )
                                )
                            },
                        )
                    } else if (isTransfer(recordType)) {
                        AccountBottomSheet(
                            wallets = data,
                            onSelectAccount = {
                                onSelectToAccount(it)
                                onDismissModal(false)
                            },
                            onAddAccount = {
                                onEventAccount(
                                    WalletEvent.ShowDialog(
                                        Wallet(
                                            walletName = ""
                                        )
                                    )
                                )
                            },
                        )
                    }
                }
            }
        }else {
            categoryState.categoryResource.let { resource ->
                ResourceHandler(
                    modifier = if (resource is Resource.Loading || resource.data.isNullOrEmpty()) Modifier.fillMaxHeight(
                        0.5f
                    ) else Modifier,
                    resource = resource,
                    nullOrEmptyMessage = "You Didn't Have Any Categories Yet",
                    isNullOrEmpty = { it.isNullOrEmpty() },
                    errorMessage = resource.message ?: "",
                    onMessageClick = {
                        onEventCategory(
                            CategoryEvent.ShowDialog(
                                Category(
                                    categoryName = ""
                                )
                            )
                        )
                    },
                ) { data ->
                    if (!isTransfer(recordType)) {
                        CategoryBottomSheet(
                            categoryMaps = data,
                            recordType = recordType,
                            onSelectCategory = {
                                onSelectCategory(it)
                                onDismissModal(false)
                            },
                            onAddCategory = {
                                onEventCategory(
                                    CategoryEvent.ShowDialog(
                                        Category(
                                            categoryName = ""
                                        )
                                    )
                                )
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
                    image = R.drawable.ic_add_foreground,
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
    categoryMaps: List<CategoryMap>,
    recordType: RecordType,
    onSelectCategory: (Category) -> Unit,
    onAddCategory: () -> Unit
) {
    val categoryMap = categoryMaps.find { c ->
        c.categoryType == recordType
    } ?: CategoryMap()
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

            items(categoryMap.categories) { category ->
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
                            R.drawable.ic_add_foreground
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