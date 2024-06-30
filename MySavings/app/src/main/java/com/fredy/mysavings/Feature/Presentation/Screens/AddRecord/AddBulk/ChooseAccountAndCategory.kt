package com.fredy.mysavings.Feature.Presentation.Screens.AddRecord.AddBulk

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.fredy.mysavings.Feature.Presentation.Util.formatBalanceAmount
import com.fredy.mysavings.Feature.Presentation.ViewModels.AddRecordViewModel.AddRecordState

@Composable
fun ChooseAccountAndMultiCategory(
    modifier: Modifier = Modifier,
    onBackground: Color = MaterialTheme.colorScheme.onBackground,
    state: AddRecordState,
    onTopButtonClick: () -> Unit,
    onLeftButtonClick: () -> Unit,
    onRightButtonClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(
                vertical = 4.dp
            ),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "From Wallet",
                color = onBackground,
            )
            SimpleButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        45.dp
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
                onClick = onTopButtonClick,
                title = state.fromWallet.walletName + " | " + formatBalanceAmount(
                    amount = state.fromWallet.walletAmount,
                    currency = state.fromWallet.walletCurrency,
                    isShortenToChar = true,
                    k = false,
                    m = false
                ),
                titleStyle = MaterialTheme.typography.headlineSmall.copy(
                    onBackground
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
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
                    text = "Expense Category",
                    color = onBackground
                )
                SimpleButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            45.dp
                        )
                        .clip(
                            MaterialTheme.shapes.small
                        )
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.secondary,
                            shape = MaterialTheme.shapes.small
                        ),
                    image = state.toCategory.categoryIcon,
                    imageDescription = state.toCategory.categoryIconDescription,
                    imageColor = if (state.toCategory.categoryIconDescription == "") onBackground else Color.Unspecified,
                    onClick = onLeftButtonClick,
                    title = state.toCategory.categoryName,
                    titleStyle = MaterialTheme.typography.headlineSmall.copy(onBackground)
                )
            }
            Column(
                modifier = Modifier.weight(
                    1f
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Income Category",
                    color = onBackground
                )
                SimpleButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            45.dp
                        )
                        .clip(
                            MaterialTheme.shapes.small
                        )
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.secondary,
                            shape = MaterialTheme.shapes.small
                        ),
                    image = state.toIncomeCategory.categoryIcon,
                    imageDescription = state.toIncomeCategory.categoryIconDescription,
                    imageColor = if (state.toIncomeCategory.categoryIconDescription == "") onBackground else Color.Unspecified,
                    onClick = onRightButtonClick,
                    title = state.toIncomeCategory.categoryName,
                    titleStyle = MaterialTheme.typography.headlineSmall.copy(onBackground)
                )
            }

        }
    }
}