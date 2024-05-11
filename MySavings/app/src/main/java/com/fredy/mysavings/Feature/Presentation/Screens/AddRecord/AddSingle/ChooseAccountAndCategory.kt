package com.fredy.mysavings.Feature.Presentation.Screens.AddRecord.AddSingle

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SimpleButton
import com.fredy.mysavings.Feature.Presentation.Util.isTransfer
import com.fredy.mysavings.Feature.Presentation.ViewModels.AddRecordViewModel.AddRecordState

@Composable
fun ChooseAccountAndCategory(
    modifier: Modifier = Modifier,
    onBackground: Color = MaterialTheme.colorScheme.onBackground,
    state: AddRecordState,
    onLeftButtonClick: () -> Unit,
    onRightButtonClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                vertical = 4.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            4.dp
        )
    ) {
        Column(
            modifier = Modifier.weight(
                1f
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isTransfer(
                        state.recordType
                    )
                ) "From" else "Wallet",
                color = onBackground,
            )
            SimpleButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        50.dp
                    )
                    .clip(
                        MaterialTheme.shapes.small
                    )
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.secondary,
                        shape = MaterialTheme.shapes.small
                    ),
                image = state.fromWallet.walletIcon,
                imageDescription = state.fromWallet.walletIconDescription,
                imageColor = if (state.fromWallet.walletIconDescription == "") onBackground else Color.Unspecified,
                onClick = onLeftButtonClick,
                title = state.fromWallet.walletName,
                titleStyle = MaterialTheme.typography.headlineSmall.copy(
                    onBackground
                )
            )
        }

        Column(
            modifier = Modifier.weight(
                1f
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isTransfer(
                        state.recordType
                    )
                ) "To" else "Category",
                color = onBackground
            )
            SimpleButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        50.dp
                    )
                    .clip(
                        MaterialTheme.shapes.small
                    )
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.secondary,
                        shape = MaterialTheme.shapes.small
                    ),
                image = if (isTransfer(
                        state.recordType
                    )
                ) state.toWallet.walletIcon else state.toCategory.categoryIcon,
                imageDescription = if (isTransfer(
                        state.recordType
                    )
                ) state.toWallet.walletIconDescription else state.toCategory.categoryIconDescription,
                imageColor = if (state.toCategory.categoryIconDescription != "" && !isTransfer(
                        state.recordType
                    )
                ) {
                    Color.Unspecified
                } else if (state.toWallet.walletIconDescription != "" && isTransfer(
                        state.recordType
                    )
                ) {
                    Color.Unspecified
                } else {
                    onBackground
                },
                onClick = onRightButtonClick,
                title = if (isTransfer(
                        state.recordType
                    )
                ) state.toWallet.walletName else state.toCategory.categoryName,
                titleStyle = MaterialTheme.typography.headlineSmall.copy(
                    onBackground
                )
            )
        }
    }
}