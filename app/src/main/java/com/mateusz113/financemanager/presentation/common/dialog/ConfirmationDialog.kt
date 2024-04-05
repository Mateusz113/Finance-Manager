package com.mateusz113.financemanager.presentation.common.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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

@Composable
fun ConfirmationDialog(
    dialogTitle: String,
    dialogText: String,
    isDialogOpen: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (isDialogOpen) {
        Dialog(
            onDismissRequest = onDismiss,
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
                    val (title, titleDivider, text, textDivider, confirm, cancel) = createRefs()
                    Text(
                        modifier = Modifier
                            .constrainAs(title) {
                                top.linkTo(parent.top)
                                centerHorizontallyTo(parent)
                            },
                        text = dialogTitle,
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    HorizontalDivider(
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
                        text = dialogText,
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontSize = MaterialTheme.typography.titleSmall.fontSize
                        )
                    )

                    HorizontalDivider(
                        modifier = Modifier
                            .constrainAs(textDivider) {
                                top.linkTo(text.bottom)
                                centerHorizontallyTo(parent)
                            }
                            .fillMaxWidth()
                            .padding(top = 15.dp)
                    )

                    TextButton(
                        onClick = onConfirm,
                        modifier = Modifier
                            .constrainAs(confirm) {
                                bottom.linkTo(parent.bottom)
                                end.linkTo(parent.end)
                                top.linkTo(textDivider.bottom)
                            }
                    ) {
                        Text(text = stringResource(id = R.string.confirm))
                    }

                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .constrainAs(cancel) {
                                bottom.linkTo(parent.bottom)
                                end.linkTo(confirm.start)
                                top.linkTo(textDivider.bottom)
                            }
                            .padding(end = 15.dp)
                    ) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                }
            }
        }
    }
}
