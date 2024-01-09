package com.mateusz113.financemanager.presentation.common.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.domain.model.PaymentListing
import com.mateusz113.financemanager.presentation.common.components.PaymentListingsInfo
import com.mateusz113.financemanager.util.Currency

@Composable
fun PaymentListingsCollectionDialog(
    paymentListings: List<PaymentListing>,
    currency: Currency,
    isCurrencyPrefix: Boolean?,
    isDialogOpen: Boolean,
    isOpen: (Boolean) -> Unit,
    onPaymentClick: (PaymentListing) -> Unit
) {
    if (isDialogOpen) {
        Dialog(
            onDismissRequest = {
                //Do not update settings when dialog is dismissed
                isOpen(false)
            },
        ) {
            Card(
                modifier = Modifier.sizeIn(
                    minWidth = 280.dp,
                    maxWidth = 560.dp,
                    minHeight = 400.dp,
                    maxHeight = 400.dp
                ),
                shape = RoundedCornerShape(size = 5.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 15.dp),
                            text = stringResource(id = R.string.expenses_list),
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Divider(
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .padding(top = 15.dp, bottom = 5.dp)
                            )
                        }
                    }
                    paymentListings.forEachIndexed { index, listing ->
                        item {
                            PaymentListingsInfo(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 16.dp,
                                        vertical = 10.dp
                                    )
                                    .height(85.dp)
                                    .clickable {
                                        onPaymentClick(listing)
                                    },
                                paymentListing = listing,
                                currency = currency,
                                isCurrencyPrefix = isCurrencyPrefix
                            )

                            if (index < paymentListings.lastIndex) {
                                Divider(
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}