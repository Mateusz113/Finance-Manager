package com.mateusz113.financemanager.presentation.payments.payment_listings.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.domain.model.PaymentListing

@Composable
fun PaymentListingsItem(
    paymentListing: PaymentListing,
    modifier: Modifier,
    deletePayment: () -> Unit
) {
    var isDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }

    Row(
        modifier = modifier
            .border(
                1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(5.dp)
            )
            .padding(start = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 5.dp)
                .weight(3f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = paymentListing.title,
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = paymentListing.category.name,
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = paymentListing.date.toString(),
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
            )
        }

        //Used to cut the title early
        Spacer(modifier = Modifier.width(10.dp))

        Divider(
            modifier = Modifier
                .fillMaxHeight(0.8f)
                .width(1.dp)
        )

        Column(
            modifier = Modifier
                .weight(1.5f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier,
                text = paymentListing.amount.toString(),
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )
            )
        }

        Divider(
            modifier = Modifier
                .fillMaxHeight(0.8f)
                .width(1.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            IconButton(
                modifier = Modifier
                    .size(80.dp),
                onClick = {
                    isDialogOpen = true
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(id = R.string.remove_payment)
                )
            }
        }
    }
    PaymentListingDeletionConfirmation(
        paymentListing = paymentListing,
        isDialogOpen = isDialogOpen,
        closeDialog = {
            isDialogOpen = false
        },
        deletePayment = {
            deletePayment()
        }
    )
}