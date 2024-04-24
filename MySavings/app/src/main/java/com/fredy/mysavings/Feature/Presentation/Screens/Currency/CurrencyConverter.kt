package com.fredy.mysavings.Feature.Presentation.Screens.Currency

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fredy.mysavings.Feature.Presentation.Screens.ZCommonComponent.SimpleDropdown
import com.fredy.mysavings.Feature.Presentation.Util.currencyCodes

@Composable
fun CurrencyConverter(
    modifier: Modifier = Modifier,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onSurface,
    fromCurrency: String,
    fromOnClick: (String) -> Unit,
    toCurrency: String,
    toOnClick: (String) -> Unit,
    fromValue: String,
    onFromValueChange: (String) -> Unit,
    toValue: String,
    onToValueChange: (String) -> Unit,

    ) {
    Row(modifier = modifier) {
        Column(
            modifier = Modifier
                .weight(0.5f)
                .padding(4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SimpleDropdown(
                textFieldColors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Unspecified,
                    unfocusedIndicatorColor = Color.Unspecified,
                ),
                list = currencyCodes,
                selectedText = fromCurrency,
                onClick = fromOnClick
            )
            TextField(
                modifier = Modifier.height(50.dp),
                shape = RectangleShape,
                value = fromValue,
                onValueChange = onFromValueChange,
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                )
            )
        }
        Column(
            modifier = Modifier
                .weight(0.5f)
                .padding(4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SimpleDropdown(
                textFieldColors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Unspecified,
                    unfocusedIndicatorColor = Color.Unspecified,
                ),
                list = currencyCodes,
                selectedText = toCurrency,
                onClick = toOnClick
            )
            TextField(
                modifier = Modifier.height(50.dp),
                shape = RectangleShape,
                value = toValue,
                onValueChange = onToValueChange,
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                )
            )
        }
    }
}