package com.mateusz113.financemanager.presentation.common.components

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.domain.enumeration.Currency
import com.mateusz113.financemanager.domain.model.PaymentListing
import com.mateusz113.financemanager.util.TestTags
import java.time.format.DateTimeFormatter

@Composable
fun PaymentListingsInfo(
    modifier: Modifier,
    paymentListing: PaymentListing,
    currency: Currency,
    isCurrencyPrefix: Boolean?,
    isDeletable: Boolean = false,
    onPaymentDelete: () -> Unit = {}
) {
    val formattedDate = DateTimeFormatter
        .ofPattern("dd/MM/yyyy")
        .format(paymentListing.date)

    Row(
        modifier = modifier
            .border(
                1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(5.dp)
            )
            .padding(start = 10.dp)
            .testTag(TestTags.PAYMENT_LISTING_INFO),
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
                text = formattedDate,
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
            )
        }

        //Used to cut the title early
        Spacer(modifier = Modifier.width(10.dp))

        VerticalDivider(
            modifier = Modifier
                .fillMaxHeight(0.8f)
                .width(1.dp)
        )

        Row(
            modifier = Modifier
                .weight(1.8f)
                .fillMaxHeight()
                .padding(horizontal = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Absolute.Center
        ) {
            if (isCurrencyPrefix == true || (isCurrencyPrefix == null && currency.isPrefix)) {
                Text(
                    text = "${currency.symbol ?: currency.name} ",
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        textAlign = TextAlign.Center
                    )
                )
            }
            Text(
                modifier = Modifier
                    .weight(1f),
                text = paymentListing.amount.toString(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = if (isCurrencyPrefix == true || (isCurrencyPrefix == null && currency.isPrefix)) {
                    TextAlign.Start
                } else if (isCurrencyPrefix == false || (isCurrencyPrefix == null && !currency.isPrefix)) {
                    TextAlign.End
                } else {
                    TextAlign.Center
                },
                style = TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )
            )
            if (isCurrencyPrefix == false || (isCurrencyPrefix == null && !currency.isPrefix)) {
                Text(
                    text = " ${currency.symbol ?: currency.name}",
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
        if (isDeletable) {
            VerticalDivider(
                modifier = Modifier
                    .fillMaxHeight(0.8f)
                    .width(1.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    modifier = Modifier
                        .size(80.dp),
                    onClick = {
                        onPaymentDelete()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = stringResource(id = R.string.remove_payment)
                    )
                }
            }
        }
    }
}
