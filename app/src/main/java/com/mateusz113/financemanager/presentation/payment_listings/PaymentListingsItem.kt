package com.mateusz113.financemanager.presentation.payment_listings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mateusz113.financemanager.domain.model.PaymentListing


@Composable
fun PaymentListingsItem(
    paymentListing: PaymentListing,
    modifier: Modifier,
    deletePayment: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = paymentListing.id,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = MaterialTheme.typography.titleLarge.fontSize
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = paymentListing.category.name,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = MaterialTheme.typography.titleLarge.fontSize
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = paymentListing.title,
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = MaterialTheme.typography.titleLarge.fontSize
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = paymentListing.amount.toString(),
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = MaterialTheme.typography.titleLarge.fontSize
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = paymentListing.date.toString(),
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = MaterialTheme.typography.titleLarge.fontSize
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = deletePayment) {
            Text(text = "Delete payment")
        }
    }
}