package com.fredy.mysavings.Feature.Presentation.Screens.Category

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
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Feature.Presentation.Util.formatBalanceAmount
import com.fredy.mysavings.Feature.Presentation.Util.formatTime
import com.fredy.mysavings.Feature.Presentation.Util.isTransfer
import com.fredy.mysavings.Feature.Presentation.ViewModels.CategoryViewModel.CategoryState
import com.fredy.mysavings.Feature.Presentation.ViewModels.RecordViewModel.RecordEvent
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.DetailAppBar
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SimpleEntityItem

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CategoryDetailBottomSheet(
    modifier: Modifier = Modifier,
    isSheetOpen: Boolean,
    onCloseBottomSheet: (Boolean) -> Unit,
    recordEvent: (RecordEvent) -> Unit,
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
                                RecordEvent.ShowDialog(item)
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

