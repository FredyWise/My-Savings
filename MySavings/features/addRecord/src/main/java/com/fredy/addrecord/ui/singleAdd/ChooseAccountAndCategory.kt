package com.fredy.addrecord.ui.singleAdd

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
import com.fredy.addrecord.viewModel.AddRecordState
import com.fredy.domain.enumsChecker.isTransfer
import com.fredy.ui.components.button.SimpleButton

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
                text = if (state.recordType.isTransfer()) "From" else "Wallet",
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
                text = if (state.recordType.isTransfer()) "To" else "Category",
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
                image = if (state.recordType.isTransfer()
                ) state.toWallet.walletIcon else state.toCategory.categoryIcon,
                imageDescription = if (state.recordType.isTransfer()
                ) state.toWallet.walletIconDescription else state.toCategory.categoryIconDescription,
                imageColor = if (state.toCategory.categoryIconDescription != "" && !state.recordType.isTransfer()
                ) {
                    Color.Unspecified
                } else if (state.toWallet.walletIconDescription != "" && state.recordType.isTransfer()
                ) {
                    Color.Unspecified
                } else {
                    onBackground
                },
                onClick = onRightButtonClick,
                title = if (state.recordType.isTransfer()
                ) state.toWallet.walletName else state.toCategory.categoryName,
                titleStyle = MaterialTheme.typography.headlineSmall.copy(
                    onBackground
                )
            )
        }
    }
}