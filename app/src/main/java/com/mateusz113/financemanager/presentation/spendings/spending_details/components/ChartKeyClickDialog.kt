package com.mateusz113.financemanager.presentation.spendings.spending_details.components

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

@Composable
fun ChartKeyClickDialog(
    paymentListings: List<PaymentListing>,
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
                                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                        )

                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 15.dp, bottom = 5.dp)
                        )
                    }
                    paymentListings.forEach { listing ->
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .padding(vertical = 5.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                ChartKeyDialogItem(
                                    modifier = Modifier
                                        .clickable {
                                            onPaymentClick(listing)
                                        },
                                    paymentListing = listing
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}