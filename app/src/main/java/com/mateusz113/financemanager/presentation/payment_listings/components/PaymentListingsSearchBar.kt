package com.mateusz113.financemanager.presentation.payment_listings.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun PaymentListingsSearchBar(
    modifier: Modifier,
    value: String,
    openFilterDialog: () -> Unit,
    searchValueChange: (String) -> Unit
) {
    Row(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            textStyle = TextStyle(
                fontSize = MaterialTheme.typography.titleMedium.fontSize
            ),
            onValueChange = { valueToSearch ->
                searchValueChange(valueToSearch)
            },
            modifier = Modifier
                .weight(3f)
                .fillMaxHeight(),
            placeholder = {
                Text(text = "Search for payment...")
            },
            singleLine = true,
            shape = RoundedCornerShape(5.dp)
        )
        OutlinedButton(
            onClick = {
                openFilterDialog()
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(start = 10.dp),
            shape = RoundedCornerShape(5.dp)
        ) {
            Text(
                text = "Filter",
                style = TextStyle(
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
            )
        }
    }
}