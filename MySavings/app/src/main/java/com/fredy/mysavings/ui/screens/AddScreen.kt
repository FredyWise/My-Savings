package com.fredy.mysavings.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fredy.mysavings.ViewModels.addViewModel
import com.fredy.mysavings.ui.component.Add.ButtonWithIcon
import com.fredy.mysavings.ui.component.Add.ButtonWithTitleAndIcon
import com.fredy.mysavings.ui.component.Add.Calculator
import com.fredy.mysavings.ui.theme.SmoothBlue

@Composable
fun AddScreen(
    modifier: Modifier = Modifier,
    spacing: Dp = 3.dp,
    addViewModel: addViewModel = viewModel<addViewModel>()
) {
    val state = addViewModel.state

    Column(
        modifier = modifier.background(SmoothBlue)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ButtonWithIcon(buttonText = "CANCEL",
                buttonIcon = Icons.Default.Close,
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        vertical = 5.dp
                    ),
                onClick = { })
            Spacer(modifier = Modifier.weight(1f))
            ButtonWithIcon(buttonText = "SAVE",
                buttonIcon = Icons.Default.Check,
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        vertical = 5.dp
                    ),
                onClick = { })
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ButtonWithIcon(buttonText = "INCOME",
                modifier = Modifier.weight(1f),
                onClick = { })
            Divider(
                modifier = Modifier
                    .height(35.dp)
                    .width(
                        2.dp
                    ), color = Color.Gray
            )
            ButtonWithIcon(buttonText = "EXPENSE",
                modifier = Modifier.weight(1f),
                onClick = { })
            Divider(
                modifier = Modifier
                    .height(35.dp)
                    .width(
                        2.dp
                    ), color = Color.Gray
            )
            ButtonWithIcon(buttonText = "TRANSFER",
                modifier = Modifier.weight(1f),
                onClick = { })
        }
        Row(
            modifier = Modifier.padding(bottom = spacing),
            horizontalArrangement = Arrangement.spacedBy(
                spacing
            )
        ) {
            ButtonWithTitleAndIcon(buttonTitle = "Account", buttonIcon = ,
                buttonText = "Account",
                modifier = Modifier.weight(1f),
                onClick = {})
            ButtonWithTitleAndIcon(buttonTitle = "Category",
                buttonText = "Category",
                modifier = Modifier.weight(1f),
                onClick = {})
        }
        Calculator(
            state = state,
            onAction = addViewModel::onAction,
            btnSpacing = spacing,
            buttonAspectRatio = 1.5f,
            modifier = Modifier.fillMaxWidth()
        )
    }


}