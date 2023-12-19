package com.mateusz113.financemanager.presentation.payments.payment_listings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.domain.model.PaymentListing

@Composable
fun PaymentListingDeletionConfirmation(
    paymentListing: PaymentListing,
    isDialogOpen: Boolean,
    closeDialog: () -> Unit,
    deletePayment: () -> Unit
) {
    if (isDialogOpen) {
        Dialog(
            onDismissRequest = closeDialog,
        ) {
            Card(
                modifier = Modifier.sizeIn(
                    minWidth = 280.dp,
                    maxWidth = 560.dp
                ),
                shape = RoundedCornerShape(size = 5.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                ConstraintLayout(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp, bottom = 5.dp)
                ) {
                    val (title, titleDivider, text, textDivider, yesButton, noButton) = createRefs()
                    Text(
                        modifier = Modifier
                            .constrainAs(title) {
                                top.linkTo(parent.top)
                                centerHorizontallyTo(parent)
                            },
                        text = stringResource(id = R.string.deletion),
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Divider(
                        modifier = Modifier
                            .constrainAs(titleDivider) {
                                top.linkTo(title.bottom)
                                centerHorizontallyTo(parent)
                            }
                            .fillMaxWidth()
                            .padding(top = 15.dp)
                    )

                    Text(
                        modifier = Modifier
                            .constrainAs(text) {
                                top.linkTo(titleDivider.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                            .padding(top = 15.dp)
                            .padding(horizontal = 16.dp),
                        text = stringResource(
                            id = R.string.deletion_text,
                            paymentListing.title
                        ),
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontSize = MaterialTheme.typography.titleSmall.fontSize
                        )
                    )

                    Divider(
                        modifier = Modifier
                            .constrainAs(textDivider) {
                                top.linkTo(text.bottom)
                                centerHorizontallyTo(parent)
                            }
                            .fillMaxWidth()
                            .padding(top = 15.dp)
                    )

                    TextButton(
                        onClick = {
                            deletePayment()
                            closeDialog()
                        },
                        modifier = Modifier
                            .constrainAs(yesButton) {
                                bottom.linkTo(parent.bottom)
                                end.linkTo(parent.end)
                                top.linkTo(textDivider.bottom)
                            }
                    ) {
                        Text(text = stringResource(id = R.string.yes))
                    }

                    TextButton(
                        onClick = {
                            closeDialog()
                        },
                        modifier = Modifier
                            .constrainAs(noButton) {
                                bottom.linkTo(parent.bottom)
                                end.linkTo(yesButton.start)
                                top.linkTo(textDivider.bottom)
                            }
                            .padding(end = 15.dp)
                    ) {
                        Text(text = stringResource(id = R.string.no))
                    }
                }
            }
        }
    }
}