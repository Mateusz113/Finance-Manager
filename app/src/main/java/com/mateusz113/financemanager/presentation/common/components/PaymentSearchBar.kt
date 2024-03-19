package com.mateusz113.financemanager.presentation.common.components

import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.mateusz113.financemanager.R

@Composable
fun PaymentSearchBar(
    modifier: Modifier,
    value: String,
    onFilterDialogOpen: () -> Unit,
    onSearchValueChange: (String) -> Unit
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
                onSearchValueChange(valueToSearch)
            },
            modifier = Modifier
                .weight(3f)
                .fillMaxHeight(),
            placeholder = {
                Text(text = stringResource(id = R.string.search_for_payment))
            },
            singleLine = true,
            shape = RoundedCornerShape(5.dp)
        )
        OutlinedButton(
            onClick = {
                onFilterDialogOpen()
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(start = 10.dp),
            shape = RoundedCornerShape(5.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = stringResource(id = R.string.filter),
                style = TextStyle(
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
            )
        }
    }
}
