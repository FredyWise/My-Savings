package com.fredy.mysavings.ui.Screens.Category

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Util.formatBalanceAmount
import com.fredy.mysavings.Util.formatTime
import com.fredy.mysavings.Util.isTransfer
import com.fredy.mysavings.ViewModels.CategoryState
import com.fredy.mysavings.ViewModels.Event.RecordsEvent
import com.fredy.mysavings.ui.Screens.ZCommonComponent.DetailAppBar
import com.fredy.mysavings.ui.Screens.ZCommonComponent.SimpleEntityItem

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CategoryDetailBottomSheet(
    modifier: Modifier = Modifier,
    isSheetOpen: Boolean,
    onCloseBottomSheet: (Boolean) -> Unit,
    recordEvent: (RecordsEvent) -> Unit,
    state: CategoryState,
    additionalAppbar: @Composable () -> Unit = { CategoryDefaultAdditionalAppBar(state) }
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    if (isSheetOpen) {
        ModalBottomSheet(
            modifier = modifier.padding(top = 24.dp),
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            onDismissRequest = {
                onCloseBottomSheet(false)
            },
            dragHandle = {},
        ) {
            DetailAppBar(
                title = "Category details",
                resource = state.recordMapsResource,
                onNavigationIconClick = {
                    onCloseBottomSheet(false)
                },
                additionalAppbar = additionalAppbar,
            ) { item, onBackgroundColor, balanceColor ->
                SimpleEntityItem(
                    modifier = Modifier
                        .padding(
                            vertical = 4.dp
                        )
                        .clickable {
                            recordEvent(
                                RecordsEvent.ShowDialog(item)
                            )
                        },
                    iconModifier = Modifier
                        .size(
                            40.dp
                        )
                        .clip(
                            shape = CircleShape
                        ),
                    icon = item.fromWallet.walletIcon,
                    iconDescription = item.fromWallet.walletIconDescription,
                    endContent = {
                        Text(
                            text = formatTime(
                                item.record.recordDateTime.toLocalTime()
                            ),
                            color = onBackgroundColor,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                ) {
                    Text(
                        text = if (isTransfer(item.record.recordType)) {
                            item.fromWallet.walletName + " -> " + item.toWallet.walletName
                        } else item.fromWallet.walletName,
                        color = onBackgroundColor,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1
                    )
                    Text(
                        text = formatBalanceAmount(
                            item.record.recordAmount,
                            item.record.recordCurrency,
                            true
                        ),
                        color = balanceColor,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryDefaultAdditionalAppBar(
    state: CategoryState,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    SimpleEntityItem(
        modifier = Modifier.padding(8.dp),
        icon = state.category.categoryIcon,
        iconModifier = Modifier
            .size(
                55.dp
            )
            .clip(
                shape = MaterialTheme.shapes.small
            ),
        iconDescription = state.category.categoryIconDescription
    ) {
        Text(
            text = state.category.categoryName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = onBackgroundColor,
            modifier = Modifier.padding(
                vertical = 3.dp
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = "Category Type: " + state.category.categoryType.name,
            style = MaterialTheme.typography.titleMedium,
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
