package com.mateusz113.financemanager.presentation.common.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mateusz113.financemanager.R

@Composable
fun <T> RadioButtonSelectionDialog(
    isDialogOpen: Boolean,
    dialogInfo: RadioButtonsDialogInfo<T>,
    onDismiss: () -> Unit,
    onOptionSelect: (T) -> Unit,
) {
    if (isDialogOpen) {
        Dialog(
            onDismissRequest = onDismiss,
        ) {
            Card(
                modifier = Modifier
                    .sizeIn(
                        minWidth = 280.dp,
                        maxWidth = 560.dp,
                        maxHeight = 400.dp
                    ),
                shape = RoundedCornerShape(size = 5.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier
                            .padding(top = 15.dp),
                        text = stringResource(id = dialogInfo.label),
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(top = 15.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 15.dp, top = 10.dp)
                ) {
                    dialogInfo.optionsList.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (option == dialogInfo.currentOption),
                                    onClick = {
                                        onOptionSelect(option)
                                    },
                                    role = Role.RadioButton
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            RadioButton(
                                modifier = Modifier.padding(start = 10.dp),
                                selected = (option == dialogInfo.currentOption),
                                onClick = {
                                    onOptionSelect(option)
                                }
                            )
                            Text(
                                modifier = Modifier.padding(start = 10.dp),
                                text = stringResource(
                                    id = dialogInfo.optionsLabelsMap[option] ?: R.string.unknown
                                ),
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }
                }
            }
        }
    }
}