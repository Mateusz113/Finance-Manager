package com.mateusz113.financemanager.presentation.payments.payment_addition.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.presentation.common.option_picker.DatePicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate

@Composable
fun PaymentAdditionDatePicker(
    modifier: Modifier = Modifier,
    date: LocalDate,
    dateText: String,
    dateChange: (LocalDate) -> Unit
) {
    val datePickerState = rememberMaterialDialogState()

    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
            modifier = Modifier
                .width(160.dp),
            shape = RoundedCornerShape(5.dp),
            readOnly = true,
            value = dateText,
            onValueChange = {},
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.EditCalendar,
                    contentDescription = stringResource(
                        id = R.string.date_picker
                    ),
                    modifier = Modifier
                        .clickable {
                            datePickerState.show()
                        }
                        .size(20.dp)
                )
            }
        )
    }
    DatePicker(
        datePickerState = datePickerState,
        date = date,
        dateValidator = { validDate ->
            validDate <= LocalDate.now()
        },
        onDateChange = { newDate ->
            dateChange(newDate)
        }
    )
}
