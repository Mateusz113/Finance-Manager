package com.mateusz113.financemanager.presentation.payments.payment_addition.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mateusz113.financemanager.R

@Composable
fun PaymentAdditionAddPhotoContainer(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(120.dp)
            .padding(horizontal = 5.dp)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline,
                RoundedCornerShape(5.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            modifier = Modifier.sizeIn(minWidth = 100.dp, minHeight = 100.dp),
            onClick = onClick
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(id = R.string.add_new_photo),
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(50.dp)
            )
        }
    }
}